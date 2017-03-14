/*
 * Accio is a program whose purpose is to study location privacy.
 * Copyright (C) 2016-2017 Vincent Primault <vincent.primault@liris.cnrs.fr>
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

package fr.cnrs.liris.accio.agent.master

import com.google.inject.Inject
import com.twitter.util.Future
import fr.cnrs.liris.accio.agent._
import fr.cnrs.liris.accio.core.scheduler.{ClusterState, EventType, Scheduler}

/**
 * @param state Cluster state.
 */
class UnregisterWorkerHandler @Inject()(state: ClusterState, lostTaskHandler: LostTaskHandler, scheduler: Scheduler)
  extends Handler[UnregisterWorkerRequest, UnregisterWorkerResponse] {

  @throws[InvalidWorkerException]
  override def handle(req: UnregisterWorkerRequest): Future[UnregisterWorkerResponse] = {
    val worker = state(req.workerId)
    worker.runningTasks.foreach(task => lostTaskHandler.handle(LostTaskRequest(worker.id, task.id)))
    state.unregister(worker.id)
    scheduler.houseKeeping(EventType.LessResource)
    Future(UnregisterWorkerResponse())
  }
}