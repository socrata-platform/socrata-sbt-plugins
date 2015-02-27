package com.socrata.sbtplugins

import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class CoveragePluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    CoveragePlugin.trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    CoveragePlugin.requires should equal(plugins.JvmPlugin && scoverage.ScoverageSbtPlugin)
  }

  test("has project settings") {
    CoveragePlugin.projectSettings.isEmpty should equal(false)
  }
}
