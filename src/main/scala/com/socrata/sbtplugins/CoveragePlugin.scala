package com.socrata.sbtplugins

import sbt._
import sbt.Keys._
import scoverage.ScoverageSbtPlugin
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

/** Wraps scoverage sbt plugin.
  *
  * Adds ```coverageIsEnabled``` and ```coverageDisable``` keys for finer control of scoverage plugin.
  * See also [[https://github.com/scoverage/sbt-scoverage]]. */
object CoveragePlugin extends AutoPlugin {
  /** When to enable this autoplugin.
    * @return On all requirements in all scopes */
  override def trigger: PluginTrigger = allRequirements
  /** Depends on these plugins.
    * @return Basic jvm, scoverage */
  override def requires: Plugins = plugins.JvmPlugin && ScoverageSbtPlugin

  /** Exposed tasks and settings. */
  object CoverageKeys {
    /** Tells whether scoverage is enabled. */
    lazy val coverageIsEnabled = taskKey[Unit]("tells whether sbt-coverage is enabled")
    /** Disables scoverage. */
    lazy val coverageDisable = taskKey[Unit]("disables sbt-coverage plugin.")
  }

  /** Settings for the project scope.
    * @return Settings to import in the project scope. */
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    CoverageKeys.coverageIsEnabled := {
      state.value.log.info("scoverage enabled: %s".format(ScoverageSbtPlugin.enabled))
    },
    CoverageKeys.coverageDisable := { ScoverageSbtPlugin.enabled = false },
    (Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn CoverageKeys.coverageDisable,
    coverageHighlighting := false,
    coverageMinimum := 100,
    coverageFailOnMinimum := false
    )
}
