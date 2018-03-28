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

package fr.cnrs.liris.accio.storage.memory

import com.google.inject.{Inject, Singleton}
import fr.cnrs.liris.accio.storage.{AbstractStorage, Storage}

/**
 * Storage giving access in-memory repositories.
 *
 * @param runs      In-memory run repository.
 * @param workflows In-memory workflow repository.
 */
@Singleton
private[storage] final class MemoryStorage @Inject()(
  override val runs: MemoryRunRepository,
  override val workflows: MemoryWorkflowRepository)
  extends AbstractStorage

object MemoryStorage {
  // Mainly present for testing purposes.
  def create: Storage = new MemoryStorage(new MemoryRunRepository, new MemoryWorkflowRepository)
}