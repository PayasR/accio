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

import com.google.common.base.MoreObjects

import scala.reflect.ClassTag

private[sparkle] class FilterDataFrame[T: ClassTag](inner: DataFrame[T], fn: T => Boolean)
  extends DataFrame[T] {

  override def toString: String = MoreObjects.toStringHelper(this).addValue(inner).toString

  override private[sparkle] def env: SparkleEnv = inner.env

  override private[sparkle] def compute(partition: Int) = inner.compute(partition).filter(fn)

  override private[sparkle] def numPartitions = inner.numPartitions
}