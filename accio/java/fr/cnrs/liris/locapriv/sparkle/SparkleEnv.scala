/*
 * Accio is a platform to launch computer science experiments.
 * Copyright (C) 2016-2018 Vincent Primault <v.primault@ucl.ac.uk>
 *
 * Accio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Accio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Accio.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnrs.liris.locapriv.sparkle

import java.util.concurrent.{Executors, TimeUnit}

import com.twitter.util.logging.Logging

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.util.{Failure, Success}

/**
 * A Sparkle environment is responsible for the execution of parallel tasks on [[DataFrame]]s.
 *
 * @param parallelism Parallelism level (i.e., number of cores to use).
 */
class SparkleEnv(parallelism: Int) extends Logging {
  require(parallelism > 0, s"Parallelism must be strictly positive: $parallelism")
  logger.info(s"Initializing Sparkle to utilize $parallelism cores")

  private[this] val executor = {
    if (1 == parallelism) Executors.newSingleThreadExecutor else Executors.newWorkStealingPool(parallelism)
  }
  private[this] implicit val ec = ExecutionContext.fromExecutor(executor)

  /**
   * Create a new dataset from an in-memory collection.
   *
   * @param elements List of keys and items.
   * @tparam T Elements' type.
   */
  def newDataset[T: ClassTag](elements: T*): DataFrame[T] = {
    new MemoryDataFrame(elements.toArray, this, parallelism)
  }

  /**
   * Create a new dataset from a data source.
   *
   * @param frame Data frame.
   * @tparam T Elements' type.
   */
  def newDataset[T: ClassTag](frame: Frame): DataFrame[T] = {
    new SourceDataFrame(frame, this, parallelism)
  }

  /**
   * Clean and stop this environment. It will not be usable after. This method is blocking.
   */
  def stop(): Unit = synchronized {
    if (!executor.isTerminated) {
      executor.shutdown()
      while (!executor.isTerminated) {
        executor.awaitTermination(100, TimeUnit.MILLISECONDS)
      }
    }
  }

  /**
   * Submit a job to this environment.
   *
   * @param dataFrame
   * @param processor
   * @tparam T
   * @tparam U
   */
  private[sparkle] def submit[T, U: ClassTag](dataFrame: DataFrame[T], processor: (Int, Iterator[T]) => U): Array[U] = {
    if (dataFrame.numPartitions == 0) {
      Array.empty
    } else if (dataFrame.numPartitions == 1) {
      Array(processor(0, dataFrame.compute(0)))
    } else {
      val futures = Seq.tabulate(dataFrame.numPartitions)(key => Future(processor(key, dataFrame.compute(key))))
      val future = Future.sequence(futures).map(_.toArray)
      Await.ready[Array[U]](future, Duration.Inf)
      future.value.get match {
        case Success(res) => res
        case Failure(e) => throw e
      }
    }
  }
}