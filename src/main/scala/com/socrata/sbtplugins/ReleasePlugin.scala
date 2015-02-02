package com.socrata.sbtplugins

import sbt._
import sbtrelease.ReleasePlugin

object ReleasePlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def buildSettings: Seq[Def.Setting[_]] = ReleasePlugin.releaseSettings
}
