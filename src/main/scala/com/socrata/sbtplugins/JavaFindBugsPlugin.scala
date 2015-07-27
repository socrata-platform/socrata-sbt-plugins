package com.socrata.sbtplugins.findbugs

import de.johoop.findbugs4sbt.FindBugs._
import de.johoop.findbugs4sbt._
import sbt.Keys._
import sbt._

object JavaFindBugsPlugin extends AutoPlugin {
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

  val configSettings: Seq[Setting[_]] = Seq(
    JavaFindBugsKeys.findbugsFailOnError := true,
    JavaFindBugsKeys.findbugsReport := {
      val report = JavaFindBugsXml(findbugsReportPath.value).report
      report.bugs.foreach(bug => state.value.log.error(bug.summarize))
      state.value.log.info(report.summary.summarize)
      (JavaFindBugsKeys.findbugsFailOnError.value, report.bugs.length) match {
        case (true, n) if n > 0 => throw new RuntimeException("Java FindBugs has errors.")
        case _ => ()
      }
    },
    JavaFindBugsKeys.findbugsReportInline := {
      findbugs.value
      JavaFindBugsKeys.findbugsReport.value
    },
    JavaFindBugsKeys.findbugsReportInline <<= JavaFindBugsKeys.findbugsReportInline dependsOn findbugs,
    (Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn JavaFindBugsKeys.findbugsReportInline
  )

  object JavaFindBugsKeys {
    val findbugsReport = TaskKey[Unit]("findbugsReport", "transform findbugs xml to sbt log.")
    val findbugsReportInline = TaskKey[Unit]("findbugsReportInline", "run findbugs and show warnings inline.")
    val findbugsFailOnError = SettingKey[Boolean]("findbugs-fail-on-error")
  }
}
