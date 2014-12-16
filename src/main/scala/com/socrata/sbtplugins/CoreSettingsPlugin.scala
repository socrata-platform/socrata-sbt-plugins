package com.socrata.sbtplugins

import sbt._
import sbt.Keys._
import scoverage.ScoverageSbtPlugin._

object CoreSettingsPlugin extends AutoPlugin {
  override def trigger = allRequirements
  override def requires = org.scalastyle.sbt.ScalastylePlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    scalaVersion := "2.10.4",
    scalacOptions ++= Seq("-Xlint", "-deprecation", "-Xfatal-warnings", "-feature")
  ) ++ instrumentSettings
}