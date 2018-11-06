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

package fr.cnrs.liris.accio.discovery

import fr.cnrs.liris.accio.testing.MemoryOpDiscovery
import fr.cnrs.liris.testing.UnitSpec

/**
 * Unit tests for [[OpRegistry]].
 */
class OpRegistrySpec extends UnitSpec {
  behavior of "OpRegistry"

  it should "return registered operators" in {
    val registry = new OpRegistry(new MemoryOpDiscovery(testing.ops))

    registry("FirstSimple") shouldBe testing.ops(0)
    registry.get("FirstSimple") shouldBe Some(testing.ops(0))

    registry("SecondSimple") shouldBe testing.ops(1)
    registry.get("SecondSimple") shouldBe Some(testing.ops(1))

    registry.ops should contain theSameElementsAs testing.ops
  }

  it should "reject unknown operators" in {
    val registry = new OpRegistry(new MemoryOpDiscovery(testing.ops))

    registry.get("Unknown") shouldBe None
    a[NoSuchElementException] shouldBe thrownBy(registry("Unknown"))
  }
}