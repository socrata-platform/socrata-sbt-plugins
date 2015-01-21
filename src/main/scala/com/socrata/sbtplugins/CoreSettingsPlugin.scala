package com.socrata.sbtplugins

import sbt._
import sbt.Keys._

/** Enables autoplugins, sets scala version and compiler flags for static analysis. */
object CoreSettingsPlugin extends AutoPlugin {
  /** When to enable this autoplugin.
    * @return On all requirements in all scopes. */
  override def trigger: PluginTrigger = allRequirements
  /** Depends on these autoplugins.
    * @return Basic jvm, style, coverage. */
  override def requires: Plugins = plugins.JvmPlugin && StylePlugin && CoveragePlugin

  /** Settings for the project scope.
    * @return Settings to import in the project scope. */
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-Xlint", "-deprecation", "-Xfatal-warnings", "-feature")
  )
}
