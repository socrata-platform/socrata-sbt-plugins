package com.socrata.sbtplugins

import sbt._
import sbt.Keys._
import scoverage.{ScoverageSbtPlugin => OriginalPlugin}
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

/** Wraps scoverage sbt plugin.
  *
  * Adds ```coverageIsEnabled``` key for finer control of scoverage plugin.
  * Uses ```coverageOff``` provided by scoverage 1.2.0
  * See also [[https://github.com/scoverage/sbt-scoverage]]. */
object CoveragePlugin extends AutoPlugin {
  /** When to enable this autoplugin.
    * @return On all requirements in all scopes */
  override def trigger: PluginTrigger = allRequirements
  /** Depends on these plugins.
    * @return Basic jvm, scoverage */
  override def requires: Plugins = plugins.JvmPlugin && OriginalPlugin

  /** Exposed tasks and settings. */
  object CoverageKeys {
    /** Tells whether scoverage is enabled. */
    lazy val coverageIsEnabled = taskKey[Unit]("tells whether sbt-coverage is enabled")
  }

  /** Settings for the project scope.
    * @return Settings to import in the project scope. */
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    CoverageKeys.coverageIsEnabled := {
      state.value.log.info("scoverage enabled: %s".format(OriginalPlugin.enabled))
    },
    (Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn coverageOff,
    coverageHighlighting := false,
    coverageMinimum := 80,
    coverageFailOnMinimum := true
    )
}
