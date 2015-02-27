package com.socrata.sbtplugins

import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class ReleasePluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    ReleasePlugin.trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    ReleasePlugin.requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    ReleasePlugin.projectSettings.isEmpty should equal(false)
  }
}
