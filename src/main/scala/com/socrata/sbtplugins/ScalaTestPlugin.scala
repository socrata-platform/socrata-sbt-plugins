package com.socrata.sbtplugins

import sbt._
import sbt.Keys._

object ScalaTestPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
  )
}
