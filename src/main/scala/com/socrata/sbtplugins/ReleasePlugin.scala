package com.socrata.sbtplugins

import sbt._
import sbtrelease.{ReleasePlugin => OriginalPlugin}

object ReleasePlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = OriginalPlugin.releaseSettings
}
