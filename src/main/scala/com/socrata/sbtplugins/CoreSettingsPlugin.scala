package com.socrata.sbtplugins

import sbt._
import sbt.Keys._

object CoreSettingsPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = org.scalastyle.sbt.ScalastylePlugin

  lazy val socrataScalastyle = taskKey[Unit]("Run scalastyle with embedded config")

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-Xlint", "-deprecation", "-Xfatal-warnings", "-feature"),
    (socrataScalastyle in Compile) := ScalastyleTask.doScalastyle(state.value,
      "/scalastyle-config.xml", "scalastyle-result.xml",
      (scalaSource in Compile).value, (target in Compile).value),
    (socrataScalastyle in Test) := ScalastyleTask.doScalastyle(state.value,
      "/scalastyle-test-config.xml", "scalastyle-test-result.xml",
      (scalaSource in Test).value, (target in Test).value),
    (test in Test) <<= (test in Test) dependsOn (socrataScalastyle in Test),
    (Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn (socrataScalastyle in Compile)
  )
}
