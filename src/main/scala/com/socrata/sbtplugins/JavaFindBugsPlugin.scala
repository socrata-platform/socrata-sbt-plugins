package com.socrata.sbtplugins

import sbt.Keys._
import sbt._
import de.johoop.findbugs4sbt.FindBugs._
import de.johoop.findbugs4sbt._

object JavaFindBugsPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = findbugsSettings ++ Seq(
    findbugsAnalyzedPath := Seq(classDirectory in Compile value),
    findbugsAnalyzeNestedArchives := false,
    findbugsEffort := Effort.Default,
    findbugsExcludeFilters := None,
    findbugsIncludeFilters := None,
    findbugsMaxMemory := 1024,
    findbugsOnlyAnalyze := None,
    findbugsPriority := Priority.Medium,
    findbugsReportType := Some(ReportType.Xml),
    findbugsReportPath := Some(crossTarget.value / "findbugs-report.xml"),
    findbugsSortReportByClassNames := false
  )
}
