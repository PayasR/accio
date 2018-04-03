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

import java.util.concurrent.ConcurrentHashMap

import com.twitter.finagle.stats.StatsReceiver
import fr.cnrs.liris.accio.api.ResultList
import fr.cnrs.liris.accio.api.thrift.Workflow
import fr.cnrs.liris.accio.storage.{WorkflowQuery, WorkflowStore}

import scala.collection.JavaConverters._
import scala.collection.concurrent

/**
 * Run repository storing data in memory. Intended for testing only.
 *
 * @param statsReceiver Stats receiver.
 */
private[memory] final class MemoryWorkflowStore(statsReceiver: StatsReceiver)
  extends WorkflowStore.Mutable {

  private[this] val index = new ConcurrentHashMap[String, Workflow].asScala
  private[this] val history = new ConcurrentHashMap[String, concurrent.Map[String, Workflow]].asScala
  statsReceiver.provideGauge("storage", "memory", "workflow", "index_size")(index.size)
  statsReceiver.provideGauge("storage", "memory", "workflow", "history_size")(history.values.map(_.size).sum)

  override def list(query: WorkflowQuery): ResultList[Workflow] = {
    val results = index.values
      .filter(query.matches)
      .toSeq
      .sortWith((a, b) => a.createdAt.get > b.createdAt.get)
    ResultList.slice(results, offset = query.offset, limit = query.limit)
  }

  override def get(id: String, version: Option[String]): Option[Workflow] =
    version match {
      case None => index.get(id)
      case Some(v) => history.get(id).flatMap(_.get(v))
    }

  override def save(workflow: Workflow): Unit = {
    history.putIfAbsent(workflow.id, new ConcurrentHashMap[String, Workflow].asScala)
    history(workflow.id)(workflow.version.get) = workflow
    index(workflow.id) = workflow
  }
}
