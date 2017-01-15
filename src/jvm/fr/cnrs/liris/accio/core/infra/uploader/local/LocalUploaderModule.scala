/*
 * Accio is a program whose purpose is to study location privacy.
 * Copyright (C) 2016 Vincent Primault <vincent.primault@liris.cnrs.fr>
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

package fr.cnrs.liris.accio.core.infra.uploader.local

import java.nio.file.Path

import com.google.inject.Provides
import fr.cnrs.liris.accio.core.service.{Configurable, Uploader}
import net.codingwell.scalaguice.ScalaModule

/**
 * Local uploader configuration.
 *
 * @param rootDir Root directory under which to store files.
 */
case class LocalUploaderConfig(rootDir: Path)

/**
 * Guice module provisioning a local uploader.
 */
final class LocalUploaderModule extends ScalaModule with Configurable[LocalUploaderConfig] {
  override def configClass: Class[LocalUploaderConfig] = classOf[LocalUploaderConfig]

  override protected def configure(): Unit = {}

  @Provides
  def providesUploader: Uploader = {
    new LocalUploader(config.rootDir)
  }
}