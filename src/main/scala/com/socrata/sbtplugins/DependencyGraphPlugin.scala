package com.socrata.sbtplugins

import sbt._
import net.virtualvoid.sbt.graph.{Plugin => OriginalPlugin}

object DependencyGraphPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = OriginalPlugin.graphSettings
}
