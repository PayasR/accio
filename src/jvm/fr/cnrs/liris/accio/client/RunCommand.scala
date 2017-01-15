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

import com.google.inject.Inject
import com.twitter.util.Stopwatch
import com.typesafe.scalalogging.StrictLogging
import fr.cnrs.liris.accio.agent.AgentService
import fr.cnrs.liris.common.flags.{Flag, FlagsProvider}
import fr.cnrs.liris.common.util.TimeUtils

case class RunFlags(
  @Flag(name = "name", help = "Run name")
  name: Option[String],
  @Flag(name = "tags", help = "Space-separated run tags")
  tags: Option[String],
  @Flag(name = "notes", help = "Run notes")
  notes: Option[String],
  @Flag(name = "repeat", help = "Number of times to repeat each run")
  repeat: Option[Int],
  @Flag(name = "seed", help = "Seed to use for unstable operators")
  seed: Option[Long],
  @Flag(name = "params", help = "Run parameters")
  params: Option[String])

@Cmd(
  name = "run",
  flags = Array(classOf[RunFlags]),
  help = "Execute an Accio workflow.",
  allowResidue = true)
class RunCommand @Inject()(agentClient: AgentService.FinagledClient)
  extends Command with StrictLogging {

  def execute(flags: FlagsProvider, out: Reporter): ExitCode = {
    if (flags.residue.size != 1) {
      out.writeln("<error>You must provide exactly one workflow/run file.</error>")
      ExitCode.CommandLineError
    } else {
      val opts = flags.as[RunFlags]
      val elapsed = Stopwatch.start()

      //val reporter = new ConsoleProgressReporter(out)
      //flags.residue.foreach(url => make(opts, workDir, url, reporter, out))

      out.writeln(s"Done in ${TimeUtils.prettyTime(elapsed())}.")
      ExitCode.Success
    }
  }

  private def parseParams(params: String): Map[String, String] = {
    val ParamRegex = "([^=]+)=(.+)".r
    params.trim.split(" ").map {
      case ParamRegex(paramName, value) => paramName -> value
      case str => throw new IllegalArgumentException(s"Invalid param (expected key=value): $str")
    }.toMap
  }
}

/*private class ConsoleProgressReporter(out: Reporter, width: Int = 80) extends ProgressReporter {
  private[this] val progress = new AtomicInteger
  private[this] var length = 0

  override def onStart(experiment: Experiment): Unit = synchronized {
    out.writeln(s"Experiment ${experiment.shortId}: <info>${experiment.name}</info>")
  }

  override def onComplete(experiment: Experiment): Unit = {}

  override def onGraphStart(run: Run): Unit = synchronized {
    out.writeln(s"  Run ${run.id.shorten}: <info>${run.name}</info>")
  }

  override def onGraphComplete(run: Run): Unit = synchronized {
    out.write(s"    ${" " * length}\r")
    length = 0
    progress.set(0)
  }

  override def onNodeStart(run: Run, node: Node): Unit = synchronized {
    val i = progress.incrementAndGet
    //val str = s"    ${node.name}: $i/${run.graph.size}"
    val str = s"    ${node.name}"
    out.write(str)
    if (str.length < length) {
      out.write(" " * (length - str.length))
    }
    out.write("\r")
    length = str.length
  }

  override def onNodeComplete(run: Run, node: Node): Unit = {}
}*/