package com.socrata.sbtplugins

import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class ScalaTestPluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    ScalaTestPlugin.trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    ScalaTestPlugin.requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    ScalaTestPlugin.projectSettings.isEmpty should equal(false)
  }
}
