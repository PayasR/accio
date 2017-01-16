package fr.cnrs.liris.accio.client.command

import java.util.{Date, Locale}

import com.google.inject.Inject
import com.twitter.util.{Await, Return, Throw}
import fr.cnrs.liris.accio.agent.AgentService
import fr.cnrs.liris.accio.core.domain._
import fr.cnrs.liris.accio.core.infra.cli.{Cmd, Command, ExitCode, Reporter}
import fr.cnrs.liris.accio.core.service.handler.GetRunRequest
import fr.cnrs.liris.common.flags.FlagsProvider
import fr.cnrs.liris.common.util.StringUtils.padTo
import org.ocpsoft.prettytime.PrettyTime

@Cmd(
  name = "inspect",
  help = "Display status of a run.",
  allowResidue = true)
class InspectCommand @Inject()(client: AgentService.FinagledClient) extends Command {
  override def execute(flags: FlagsProvider, out: Reporter): ExitCode = {
    if (flags.residue.isEmpty || flags.residue.size > 2) {
      out.writeln("<error>You must provide exactly one run identifier.</error>")
      ExitCode.CommandLineError
    } else {
      val f = client.getRun(GetRunRequest(RunId(flags.residue.head))).liftToTry
      Await.result(f) match {
        case Return(resp) =>
          resp.result match {
            case None =>
              out.writeln(s"<error>[ERROR]</error> Unknown run: ${flags.residue.head}")
              ExitCode.CommandLineError
            case Some(run) =>
              if (flags.residue.size == 2) {
                run.state.nodes.find(_.nodeName == flags.residue.last) match {
                  case None =>
                    out.writeln(s"<error>[ERROR]</error> Unknown node: ${flags.residue.last}")
                    ExitCode.CommandLineError
                  case Some(node) => printNode(node, out)
                }
              } else {
                printRun(run, out)
              }
              ExitCode.Success
          }
        case Throw(e) =>
          out.writeln(s"<error>[ERROR]</error> Server error: ${e.getMessage}")
          ExitCode.InternalError
      }
    }
  }

  private def printRun(run: Run, out: Reporter) = {
    val prettyTime = new PrettyTime().setLocale(Locale.ENGLISH)
    out.writeln(s"<comment>${padTo("Id", 15)}</comment> ${run.id.value}")
    out.writeln(s"<comment>${padTo("Workflow", 15)}</comment> ${run.pkg.workflowId.value}:${run.pkg.workflowVersion}")
    out.writeln(s"<comment>${padTo("Created", 15)}</comment> ${prettyTime.format(new Date(run.createdAt))}")
    out.writeln(s"<comment>${padTo("Owner", 15)}</comment> ${Utils.toString(run.owner)}")
    out.writeln(s"<comment>${padTo("Name", 15)}</comment> ${run.name.getOrElse("<no name>")}")
    out.writeln(s"<comment>${padTo("Tags", 15)}</comment> ${if (run.tags.nonEmpty) run.tags.mkString(", ") else "<none>"}")
    out.writeln(s"<comment>${padTo("Status", 15)}</comment> ${run.state.status.name}")
    if (!Utils.isCompleted(run.state.status)) {
      out.writeln(s"<comment>${padTo("Progress", 15)}</comment> ${(run.state.progress * 100).round}")
    }
    run.state.startedAt.foreach { startedAt =>
      out.writeln(s"<comment>${padTo("Started", 15)}</comment> ${prettyTime.format(new Date(startedAt))}")
    }
    run.state.completedAt.foreach { completedAt =>
      out.writeln(s"<comment>${padTo("Completed", 15)}</comment> ${prettyTime.format(new Date(completedAt))}")
    }
    out.writeln()
    out.writeln(s"<comment>${padTo("Node name", 25)}  ${padTo("Status", 9)}  Result</comment>")
    run.state.nodes.foreach { node =>
      val result = node.result.map { result =>
        if (result.exitCode != 0) {
          s"Exited with ${result.exitCode}${result.error.map(err => ". " + err.root.classifier + ": " + err.root.message).getOrElse("")}"
        } else {
          s"Artifacts: ${result.artifacts.map(_.name).mkString(",")}"
        }
      }.getOrElse("<none>")
      out.writeln(s"${padTo(node.nodeName, 25)}  ${padTo(node.status.name, 9)}  $result")
    }
  }

  private def printNode(node: NodeState, out: Reporter) = {
    val prettyTime = new PrettyTime().setLocale(Locale.ENGLISH)
    out.writeln(s"<comment>${padTo("Node name", 15)}</comment> ${node.nodeName}")
    out.writeln(s"<comment>${padTo("Status", 15)}</comment> ${node.status.name}")
    node.startedAt.foreach { startedAt =>
      out.writeln(s"<comment>${padTo("Started", 15)}</comment> ${prettyTime.format(new Date(startedAt))}")
    }
    node.completedAt.foreach { completedAt =>
      out.writeln(s"<comment>${padTo("Completed", 15)}</comment> ${prettyTime.format(new Date(completedAt))}")
    }

    node.result.foreach { result =>
      out.writeln()
      out.writeln(s"<comment>${padTo("Artifact name", 25)}  Value</comment>")
      result.artifacts.foreach { artifact =>
        out.writeln(s"${padTo(artifact.name, 25)}  ${Values.toString(artifact.value, artifact.kind)}</comment>")
      }

      out.writeln()
      out.writeln(s"<comment>${padTo("Metric name", 25)}  Value</comment>")
      result.metrics.foreach { metric =>
        out.writeln(s"${padTo(metric.name, 25)}  ${metric.value}")
      }
    }
  }
}
