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

package fr.cnrs.liris.util.geo

/**
 * Conversions between doubles and distances.
 */
object conversions {

  /**
   * Provides new methods on a double to convert it into a distance.
   *
   * @param wrapped Wrapped double value.
   */
  class RichNumber(wrapped: Double) {
    def meter = meters

    def meters = Distance.meters(wrapped.abs)

    def kilometer = kilometers

    def kilometers = Distance.kilometers(wrapped.abs)

    def mile = miles

    def miles = Distance.miles(wrapped.abs)
  }

  implicit def intToRichNumber(i: Int): RichNumber = new RichNumber(i)

  implicit def longToRichNumber(l: Long): RichNumber = new RichNumber(l)

  implicit def doubleToRichNumber(d: Double): RichNumber = new RichNumber(d)
}
