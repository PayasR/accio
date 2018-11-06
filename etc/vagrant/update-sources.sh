#!/bin/bash
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

rsync -urzvhl /vagrant/ /home/vagrant/accio \
    --filter=':- /vagrant/.gitignore' \
    --exclude=.git \
    --exclude=bazel-accio \
    --exclude=bazel-bin \
    --exclude=bazel-genfiles \
    --exclude=bazel-out \
    --exclude=bazel-testlogs \
    --exclude=docs \
    --delete \

# Install/update the upstart configurations.
sudo cp /vagrant/etc/vagrant/systemd/*.service /etc/systemd/system
sudo systemctl daemon-reload