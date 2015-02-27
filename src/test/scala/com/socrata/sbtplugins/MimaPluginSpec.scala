package com.socrata.sbtplugins

import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class MimaPluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    MimaPlugin.trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    MimaPlugin.requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    MimaPlugin.projectSettings.isEmpty should equal(false)
  }
}
