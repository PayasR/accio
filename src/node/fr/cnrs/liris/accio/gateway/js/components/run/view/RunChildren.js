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

import React from 'react'
import moment from 'moment'
import {Link} from 'react-router'
import {map, forEach, toPairs} from 'lodash'
import {ProgressBar, Table, Glyphicon} from 'react-bootstrap'
import {prettyPrintValue} from '../../../utils/prettyPrint'

class RunChildren extends React.Component {
  render() {
    let paramsValues = {}
    this.props.runs.forEach(run => {
      forEach(run.params, (value, key) => {
        if (!(key in paramsValues)) {
          paramsValues[key] = new Set()
        }
        if (paramsValues[key].size < 2) {
          paramsValues[key].add(value.payload)
        }
      })
    })
    const commonParams = toPairs(paramsValues).filter(kv => kv[1].size == 1).map(kv => kv[0])

    const rows = this.props.runs.map((run, idx) => {
      const style = (run.state.completed_at) ? (run.state.status == 'success') ? 'success' : 'danger' : 'warning'
      const progress = Math.round(run.state.progress * 100)
      const name = toPairs(run.params)
          .filter(kv => commonParams.indexOf(kv[0]) == -1)
          .map(kv => kv[0] + '=' + prettyPrintValue(kv[1].payload, kv[1].kind))
          .sort()
          .join(' ')
      return <tr key={idx}>
        <td>
          <Link to={'/runs/view/' + run.id}>{name}</Link>
          {run.children ? <span style={{marginLeft: '10px'}}><Glyphicon glyph="tasks"/>&nbsp;{run.children}</span> : null}
        </td>
        <td><ProgressBar now={progress} label={progress + '%'} bsStyle={style}/></td>
        <td>{(run.state.started_at) ? moment(run.state.started_at).fromNow() : '–'}</td>
      </tr>
    });
    return <Table striped hover responsive className="accio-list-table">
      <thead>
      <tr>
        <th>Parameters</th>
        <th>Progress</th>
        <th>Started</th>
      </tr>
      </thead>
      <tbody>
      {rows}
      </tbody>
    </Table>
  }
}

RunChildren.propTypes = {
  runs: React.PropTypes.array.isRequired,
}

export default RunChildren;