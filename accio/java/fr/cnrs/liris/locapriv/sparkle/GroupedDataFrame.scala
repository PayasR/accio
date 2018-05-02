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

import scala.reflect.ClassTag

private[sparkle] class GroupedDataFrame[K: ClassTag, V: ClassTag](inner: DataFrame[V], fn: V => K)
  extends DataFrame[(K, V)] {

  private[this] val index: Array[Array[Int]] = ???

  override private[sparkle] def numPartitions = index.length

  override private[sparkle] def compute(partition: Int) = new PartialIterator[(K, V)](index(partition), ???)

  override private[sparkle] def env = inner.env
}
