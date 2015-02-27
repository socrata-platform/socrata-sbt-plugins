package com.socrata.sbtplugins

import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class CoreSettingsPluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    CoreSettingsPlugin.trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    CoreSettingsPlugin.requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    CoreSettingsPlugin.projectSettings.isEmpty should equal(false)
  }
}
