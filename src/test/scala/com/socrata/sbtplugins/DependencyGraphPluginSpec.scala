package com.socrata.sbtplugins

import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class DependencyGraphPluginSpec extends FunSuiteLike with Matchers {
   test("triggers on all requirements") {
     DependencyGraphPlugin.trigger should equal(AllRequirements)
   }

   test("depends on jvm plugin") {
     DependencyGraphPlugin.requires should equal(plugins.JvmPlugin)
   }

   test("has project settings") {
     DependencyGraphPlugin.projectSettings.isEmpty should equal(false)
   }
 }
