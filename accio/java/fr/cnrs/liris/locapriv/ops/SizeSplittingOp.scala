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

package fr.cnrs.liris.locapriv.ops

import fr.cnrs.liris.accio.sdk._
import fr.cnrs.liris.locapriv.domain.{Event, Trace}

@Op(
  category = "transform",
  help = "Split traces, ensuring a maximum size for each one.",
  cpus = 4,
  ram = "2G")
case class SizeSplittingOp(
  @Arg(help = "Maximum number of events allowed in each trace")
  size: Int,
  @Arg(help = "Input dataset")
  data: RemoteFile)
  extends ScalaOperator[SizeSplittingOut] with SlidingSplitting with SparkleOperator {

  override def execute(ctx: OpContext): SizeSplittingOut = {
    val split = (buffer: Seq[Event], _: Event) => buffer.size >= size
    val output = read[Trace](data).flatMap(transform(_, split))
    SizeSplittingOut(write(output, ctx))
  }
}

case class SizeSplittingOut(
  @Arg(help = "Output dataset")
  data: RemoteFile)