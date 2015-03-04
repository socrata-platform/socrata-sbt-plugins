package com.socrata.sbtplugins

import com.socrata.sbtplugins.CoreSettingsPlugin.{ScalaVersion, _}
import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class CoreSettingsPluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    projectSettings.isEmpty should equal(false)
  }

  test("support scala versions 2.8, 2.9, 2.10, 2.11") {
    List("2.8.a", "2.9.b", "2.10.c", "2.11.d") map {
      case ScalaVersion.Is28() => () // success
      case ScalaVersion.Is29() => () // success
      case ScalaVersion.Is210() => () // success
      case ScalaVersion.Is211() => () // success
      case v: Any => fail(s"version $v not supported")
    }
  }

  test("format dir by scala major minor version") {
    val d = dir4ScalaV("123.456.789")
    d should equal("scala-123.456")
  }

  test("format dir by scala version throws") {
    a[UnsupportedVersionError] shouldBe thrownBy {
      val d = dir4ScalaV("foo.bar.42")
    }
  }
}
