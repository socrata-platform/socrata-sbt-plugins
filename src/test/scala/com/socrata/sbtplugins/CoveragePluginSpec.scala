package com.socrata.sbtplugins

import com.socrata.sbtplugins.CoveragePlugin._
import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class CoveragePluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    requires should equal(plugins.JvmPlugin && scoverage.ScoverageSbtPlugin)
  }

  test("has project settings") {
    projectSettings.isEmpty should equal(false)
  }
}
