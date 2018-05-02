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

import fr.cnrs.liris.testing.UnitSpec
import org.scalatest.BeforeAndAfterEach

import scala.collection.mutable
import scala.reflect._

/**
 * Unit tests for [[DataFrame]].
 */
class DataFrameSpec extends UnitSpec with BeforeAndAfterEach {
  behavior of "Dataset"

  private[this] var env: SparkleEnv = _

  override def beforeEach(): Unit = {
    super.beforeEach()
    env = new SparkleEnv(parallelism = 1)
  }

  override def afterEach(): Unit = {
    env.stop()
    env = null
    super.afterEach()
  }

  // Transformation operations.
  it should "map elements" in {
    val data = env.newDataset(1, 2, 3, 4, 5)
    data.map(_ * 2).toArray shouldBe Array(2, 4, 6, 8, 10)
  }

  it should "flatMap elements" in {
    val data = env.newDataset(1, 2, 3, 4, 5)
    data.flatMap(i => Set(i, i * 2)).toArray shouldBe Array(1, 2, 2, 4, 3, 6, 4, 8, 5, 10)
  }

  it should "filter elements" in {
    val data = env.newDataset(1, 2, 3, 4, 5)
    data.filter(i => (i % 2) == 0).toArray shouldBe Array(2, 4)
  }

  it should "zip with another dataset with same keys and same size" in {
    val data1 = env.newDataset(1, 2, 3, 4, 5)
    val data2 = env.newDataset("foo" -> Seq(2, 4, 6), "bar" -> Seq(8, 10))
    data1.zip(data2).toArray shouldBe Array((1, 2), (2, 4), (3, 6), (4, 8), (5, 10))
  }

  it should "zip with another dataset with different keys" in {
    val data1 = env.newDataset("foo" -> Seq(1, 2, 3), "foobar" -> Seq(4))
    val data2 = env.newDataset("bar" -> Seq(8, 10), "foobar" -> Seq(8))
    data1.zip(data2).toArray shouldBe Array((4, 8))
  }

  it should "zip with another dataset with different size" in {
    val data1 = env.newDataset("foo" -> Seq(1, 2, 3), "bar" -> Seq(3))
    val data2 = env.newDataset("foo" -> Seq(2, 4), "bar" -> Seq(6, 23))
    data1.zip(data2).toArray shouldBe Array((1, 2), (2, 4), (3, 6))
  }

  // Terminal operations.
  it should "count elements" in {
    val data = env.newDataset(1, 2, 3, 4, 5)
    data.count() shouldBe 5
  }

  it should "return its elements in order" in {
    val data = env.newDataset(1, 2, 3, 4, 5)
    data.toArray shouldBe Array(1, 2, 3, 4, 5)
  }

  it should "apply an operation on each element" in {
    val data = env.newDataset(1, 2, 3, 4, 5)
    val res = mutable.Set.empty[Int]
    data.foreach { i =>
      res synchronized {
        res += i
      }
    }
    res shouldBe Set(1, 2, 3, 4, 5)
  }
}