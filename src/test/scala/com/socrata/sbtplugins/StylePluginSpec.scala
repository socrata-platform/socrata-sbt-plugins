package com.socrata.sbtplugins

import java.util.jar.JarFile

import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class StylePluginSpec extends FunSuiteLike with Matchers{
  test("triggers on all requirements") {
    StylePlugin.trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    StylePlugin.requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    StylePlugin.projectSettings.isEmpty should equal(false)
  }
}
