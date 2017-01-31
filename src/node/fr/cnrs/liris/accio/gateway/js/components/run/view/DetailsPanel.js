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

import React from "react"
import {Link} from "react-router"
import {Row, Col, Panel} from "react-bootstrap"
import {map} from 'lodash'
import Username from '../../Username'

let DetailsPanel = React.createClass({
  render: function () {
    const {run} = this.props
    return (
      <Panel header="Run details"
             className="accio-view-panel"
             collapsible={true}
             defaultExpanded={true}>
        <Row>
          <Col sm={2} className="accio-view-label">Workflow</Col>
          <Col sm={10}>
            <Link to={'workflows/view/' + run.pkg.workflow_id + '/' + run.pkg.workflow_version}>
              {run.pkg.workflow_id}:{run.pkg.workflow_version}
            </Link>
          </Col>
        </Row>
        {run.parent ?
          <Row>
            <Col sm={2} className="accio-view-label">Parent</Col>
            <Col sm={10}><Link to={'/runs/view/' + run.parent.id}>{run.parent.name ? run.parent.name : 'Untitled run #' + run.parent.id}</Link></Col>
          </Row> : null}
        <Row>
          <Col sm={2} className="accio-view-label">Owner</Col>
          <Col sm={10}><Username user={run.owner}/></Col>
        </Row>
        <Row>
          <Col sm={2} className="accio-view-label">Seed</Col>
          <Col sm={10}>{run.seed}</Col>
        </Row>
      </Panel>
    );
  }
});

DetailsPanel.propTypes = {
  run: React.PropTypes.object.isRequired,
};

export default DetailsPanel;