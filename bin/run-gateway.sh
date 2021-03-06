#!/usr/bin/env bash
# Accio is a platform to launch computer science experiments.
# Copyright (C) 2016-2018 Vincent Primault <v.primault@ucl.ac.uk>
#
# Accio is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# Accio is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with Accio.  If not, see <http://www.gnu.org/licenses/>.

bazel build accio/java/fr/cnrs/liris/accio/tools/gateway
./bazel-bin/accio/java/fr/cnrs/liris/accio/tools/gateway/gateway \
  -admin.port=":9991" \
  -http.port=":8888" \
  -ui \
  -agent.server=localhost:9999 \
  "$@"
