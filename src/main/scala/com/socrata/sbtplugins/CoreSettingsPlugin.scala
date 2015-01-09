package com.socrata.sbtplugins

import sbt._
import sbt.Keys._

object CoreSettingsPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin && StylePlugin && CoveragePlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-Xlint", "-deprecation", "-Xfatal-warnings", "-feature")
  )
}
