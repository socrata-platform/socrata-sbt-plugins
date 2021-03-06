package com.socrata.sbtplugins.findbugs

import de.johoop.findbugs4sbt.FindBugs._
import de.johoop.findbugs4sbt._
import org.apache.commons.io.filefilter.HiddenFileFilter
import sbt.Keys._
import sbt._

object JavaFindBugsPlugin extends AutoPlugin {
  import JavaFindBugsKeys._ // scalastyle:ignore import.grouping
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Setting[_]] = findbugsSettings ++
    inConfig(Compile)(configSettings) ++
    inConfig(Test)(configSettings) ++ Seq(
      findbugsEffort := Effort.Minimum,
      findbugsPriority := Priority.High,
      findbugsAnalyzeNestedArchives := false,
      findbugsReportPath := Some(target.value / "findbugs-result.xml"),
      findbugsReportType := Some(ReportType.Xml)
    )

  val findbugsIfPathNonEmpty = Def.taskDyn[Unit] {
    val classFileFilter = HiddenFileFilter.VISIBLE
    val numChildren: Seq[Int] = for {
      path <- findbugsAnalyzedPath.value.filter(_.isDirectory)
    } yield { path.list(classFileFilter).length }

    if (numChildren.sum > 0) {
      state.value.log.info(s"Java FindBugs starting.")
      findbugs
    } else {
      state.value.log.info(s"Java FindBugs skipped an empty target.")
      Def.task[Unit](())
    }
  }

  val findbugsReportInlineSequential = sbtsequential.DefOps.sequentialTask[Unit] {
    findbugsIfPathNonEmpty.value
    findbugsReport.value
  }

  object JavaFindBugsKeys {
    val findbugsReport = TaskKey[Unit]("findbugsReport", "transform findbugs xml to sbt log.")
    val findbugsReportInline = TaskKey[Unit]("findbugsReportInline", "run findbugs and show warnings inline.")
    val findbugsFailOnError = SettingKey[Boolean]("findbugs-fail-on-error")
  }

  val configSettings: Seq[Setting[_]] = Seq(
    findbugsFailOnError := true,
    findbugsFailOnError in Test := false,
    findbugsReport := {
      val reportPath = findbugsReportPath.value.getOrElse(throw new IllegalArgumentException)
      JavaFindBugsXml(reportPath) match {
        case JavaFindBugsXml(Some(report)) =>
          report.bugs.foreach(bug => state.value.log.error(bug.summarize))

          state.value.log.info(report.summary.summarize)
          (findbugsFailOnError.value, report.bugs.length) match {
            case (_, n) if n <= 0 => state.value.log.success(s"Java FindBugs passed.")
            case (true, n) => throw new RuntimeException(s"Java FindBugs has $n errors.")
            case (false, n) => state.value.log.warn(s"Java FindBugs has $n errors.")
          }
        case _ => ()
      }
    },
    findbugsReportInline := {
      findbugsReportInlineSequential.value
    },
    (Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn JavaFindBugsKeys.findbugsReportInline
  )
}
