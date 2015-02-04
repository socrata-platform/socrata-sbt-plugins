package com.socrata.sbtplugins

import sbt._
import com.typesafe.tools.mima.plugin.{MimaPlugin => OriginalPlugin}

object MimaPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = OriginalPlugin.mimaDefaultSettings
}
