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

package fr.cnrs.liris.accio.storage.mysql

import com.twitter.finagle.mysql.{Client, RawValue, Row}
import com.twitter.util.{Await, Future}
import fr.cnrs.liris.accio.api.ResultList
import fr.cnrs.liris.accio.api.thrift.{Workflow, WorkflowId}
import fr.cnrs.liris.accio.storage.{WorkflowQuery, WorkflowStore}
import fr.cnrs.liris.common.scrooge.BinaryScroogeSerializer

private[this] final class MysqlWorkflowStore(client: Client) extends WorkflowStore.Mutable {
  override def list(query: WorkflowQuery): ResultList[Workflow] = {
    val f = client
      .prepare(s"select content from workflows where is_active = 1")
      .select()(decode)
      .map { workflows =>
        val results = workflows
          .filter(query.matches)
          .sortWith((a, b) => a.createdAt.get > b.createdAt.get)
        ResultList.slice(results, offset = query.offset, limit = query.limit)
      }
    Await.result(f)
  }

  override def get(id: WorkflowId): Option[Workflow] = {
    val f = client
      .prepare("select content from workflows where id = ? and is_active = 1")
      .select(id.value)(decode)
      .map(_.headOption)
    Await.result(f)
  }

  override def get(id: WorkflowId, version: String): Option[Workflow] = {
    val f = client
      .prepare("select content from workflows where id = ? and version = ?")
      .select(id.value, version)(decode)
      .map(_.headOption)
    Await.result(f)
  }

  override def save(workflow: Workflow): Unit = {
    val content = BinaryScroogeSerializer.toBytes(workflow)
    val f1 = client
      .prepare("update workflows set is_active = 0 where id = ? and version <> ?")
      .apply(workflow.id.value, workflow.version.get)
    val f2 = client
      .prepare("insert into workflows(id, version, is_active, content) values(?, ?, 1, ?)")
      .apply(workflow.id.value, workflow.version.get, content)
    Await.result(Future.join(f1, f2))
  }

  private def decode(row: Row): Workflow =
    row("content").get match {
      case raw: RawValue => BinaryScroogeSerializer.fromBytes(raw.bytes, Workflow)
      case v => throw new RuntimeException(s"Unexpected content value: $v")
    }
}