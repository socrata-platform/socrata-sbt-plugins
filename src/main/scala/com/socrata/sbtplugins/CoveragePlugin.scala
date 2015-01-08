package com.socrata.sbtplugins

import sbt._
import sbt.Keys._
import scoverage.ScoverageSbtPlugin
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

object CoveragePlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin && ScoverageSbtPlugin

  override def projectSettings: Seq[Def.Setting[_]] =
    ScoverageSbtPlugin.projectSettings ++ Seq(
    coverageHighlighting := false,
    coverageMinimum := 100,
    coverageFailOnMinimum := false,
    (test in Test) <<= (test in Test) dependsOn (coverage in Test)
    )
}
