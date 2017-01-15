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

package fr.cnrs.liris.accio.client

import ch.qos.logback.classic.{Level, Logger}
import com.google.inject.Inject
import com.typesafe.scalalogging.StrictLogging
import fr.cnrs.liris.common.flags.{FlagsParser, Priority}
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

/**
 * The command dispatcher is responsible for discovering the command to execute, instantiating it and executing it.
 *
 * @param registry Command registry.
 * @param factory  Command factory.
 * @param rcParser Accio RC files parser.
 */
class CmdDispatcher @Inject()(registry: CmdRegistry, factory: CmdFactory, rcParser: AccioRcParser) extends StrictLogging {
  /**
   * Execute the appropriate command given some arguments.
   *
   * @param args Input arguments.
   * @param out  Output where to write.
   * @return Exit code.
   */
  def exec(args: Seq[String], out: Reporter): ExitCode = {
    val cmdNamePos = args.indexWhere(s => !s.startsWith("-"))
    val (commonArgs, cmdName, otherArgs) = if (cmdNamePos > -1) {
      (args.take(cmdNamePos), args(cmdNamePos), args.drop(cmdNamePos + 1))
    } else {
      (args, "help", Seq.empty[String])
    }

    val meta = registry.get(cmdName) match {
      case None =>
        out.writeln(s"<error>Unknown command '$cmdName'</error>")
        registry("help")
      case Some(m) => m
    }

    val parser = FlagsParser(meta.defn.allowResidue, meta.defn.flags ++ Seq(classOf[AccioFlags]): _*)
    parser.parseAndExitUponError(commonArgs)
    val commonOpts = parser.as[AccioFlags]

    val accioRcArgs = rcParser.parse(commonOpts.accioRcPath, commonOpts.accioRcConfig, cmdName)
    parser.parseAndExitUponError(accioRcArgs, Priority.RcFile)
    parser.parseAndExitUponError(otherArgs)

    // Configure logging level for Accio-related code. Other logging configuration is done in an ordinary
    // logback.xml loaded at the very beginning of the main.
    val logLevel = Level.toLevel(commonOpts.logLevel)
    LoggerFactory.getLogger("fr.cnrs.liris.accio").asInstanceOf[Logger].setLevel(logLevel)
    logger.info(s"Set logging level: $logLevel")

    try {
      val command = factory.create(meta)
      command.execute(parser, out)
    } catch {
      case e: IllegalArgumentException =>
        out.writeln(s"<error>${e.getMessage.stripPrefix("requirement failed: ")}</error>")
        ExitCode.RuntimeError
      case NonFatal(e) =>
        logger.error("Uncaught exception", e)
        ExitCode.InternalError
    }
  }
}