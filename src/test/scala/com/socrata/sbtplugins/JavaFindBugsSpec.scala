package com.socrata.sbtplugins.findbugs

import com.socrata.sbtplugins.findbugs.JavaFindBugsPlugin._
import org.scalatest.{FunSuiteLike, Matchers}
import _root_.sbt.{Level => _, _}

class JavaFindBugsSpec extends FunSuiteLike with Matchers {
  val exampleFilename = "./src/test/resources/findbugs-result-example.xml"
  val emptyFilename = "./src/test/resources/empty.xml"

  test("triggers on all requirements") {
    trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    projectSettings.isEmpty should equal(false)
  }
  
  test("load example findbugs report xml") {
    val example = file(exampleFilename)
    val report = JavaFindBugsXml(example).report
    report.get.bugs.map(_.summarize).length should be(30)
    Option(report.get.summary.summarize) should be('defined)
  }

  test("load empty findbugs report xml") {
    val empty = file(emptyFilename)
    val report = JavaFindBugsXml(empty).report
    report should be(None)
  }
}
