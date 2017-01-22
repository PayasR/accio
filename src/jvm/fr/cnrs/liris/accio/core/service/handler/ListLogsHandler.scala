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

package fr.cnrs.liris.accio.core.service.handler

import com.google.inject.Inject
import com.twitter.util.{Future, Time}
import fr.cnrs.liris.accio.core.domain.{LogsQuery, RunRepository}

/**
 * Handler retrieving logs matching some search criteria.
 *
 * @param repository Run repository.
 */
class ListLogsHandler @Inject()(repository: RunRepository) extends Handler[ListLogsRequest, ListLogsResponse] {
  override def handle(req: ListLogsRequest): Future[ListLogsResponse] = {
    val query = LogsQuery(
      runId = req.runId,
      nodeName = req.nodeName,
      limit = req.limit,
      since = req.since.map(Time.fromMilliseconds))
    val results = repository.find(query)
    Future(ListLogsResponse(results))
  }
}