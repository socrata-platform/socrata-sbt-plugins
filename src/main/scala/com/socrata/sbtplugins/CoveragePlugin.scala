package com.socrata.sbtplugins

import sbt._
import sbt.Keys._
import scoverage.ScoverageSbtPlugin
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

object CoveragePlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin && ScoverageSbtPlugin

  lazy val coverageIsEnabled = taskKey[Unit]("tells whether sbt-coverage is enabled")
  lazy val coverageDisable = taskKey[Unit]("disables sbt-coverage plugin.")

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    coverageIsEnabled := { state.value.log.info("scoverage enabled: %s".format(ScoverageSbtPlugin.enabled)) },
    coverageDisable := { ScoverageSbtPlugin.enabled = false },
    (Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn (coverageDisable),
    coverageHighlighting := false,
    coverageMinimum := 100,
    coverageFailOnMinimum := false
    )
}
