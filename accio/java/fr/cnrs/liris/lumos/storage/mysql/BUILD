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

scala_library(
  name = "mysql",
  srcs = glob(["*.scala"]),
  deps = [
    "//accio/java/fr/cnrs/liris/lumos/storage",
    "//accio/java/fr/cnrs/liris/lumos/domain",
    "//accio/java/fr/cnrs/liris/lumos/domain/thrift",
    "//accio/java/fr/cnrs/liris/util/scrooge",
    "//3rdparty/jvm/com/twitter:finagle_mysql",
    "//3rdparty/jvm/com/twitter:util_slf4j_api",
  ],
  exports = [
    "//3rdparty/jvm/com/twitter:finagle_mysql",
  ],
  visibility = ["//visibility:public"],
)
