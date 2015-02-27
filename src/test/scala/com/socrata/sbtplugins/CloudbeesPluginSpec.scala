package com.socrata.sbtplugins

import org.scalatest.{Matchers, FunSuiteLike}
import sbt._
import sbtrelease.{Subversion, Mercurial, Git}
import xsbti.{AppProvider, AppConfiguration}

class CloudbeesPluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    CloudbeesPlugin.trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    CloudbeesPlugin.requires should equal(plugins.JvmPlugin && MimaPlugin && ReleasePlugin && WebDavPlugin)
  }

  test("has project settings") {
    CloudbeesPlugin.projectSettings.isEmpty should equal(false)
  }

  val dotfile: File = file(".")
  test("get vcs git") {
    val git = CloudbeesPlugin.gitOrHalt(Some(new Git(dotfile)))
  }

  test("get vcs mercurial throws") {
    a[RuntimeException] should be thrownBy {
      val merc = CloudbeesPlugin.gitOrHalt(Some(new Mercurial(dotfile)))
    }
  }

  test("get vcs subversion throws") {
    a[RuntimeException] should be thrownBy {
      val svn = CloudbeesPlugin.gitOrHalt(Some(new Subversion(dotfile)))
    }
  }

  test("git last release tag search command") {
    val git = new GitMock(dotfile)
    val tag = CloudbeesPlugin.lastReleaseTag(git)
    tag should equal("describe --tags --abbrev=0 --match=v[0-9]*")
  }

  val lastReleaseText = "v42"
  test("git log") {
    val git = new GitMock(dotfile)
    val tag = CloudbeesPlugin.gitLog(git, lastReleaseText)
    tag should equal("log v42..HEAD --pretty=format:%h %<(20)%an %ci %s")
  }

  val changeLogText =
 """
   |** Changes since release v42: **
   |this is a new commit
 """.stripMargin
  test("change log") {
    val cl = CloudbeesPlugin.changeLog(lastReleaseText, "this is a new commit", new TestLogger)
    cl should equal(changeLogText)
  }

  val yes = "y"
  val no = "n"
  val maybe = " "
  test("continue release yes") {
    CloudbeesPlugin.continueRelease(changeLogText, new LineReaderMock(Seq(yes)), new TestLogger) should equal(true)
  }

  test("continue release no") {
    a[RuntimeException] should be thrownBy {
      CloudbeesPlugin.continueRelease(changeLogText, new LineReaderMock(Seq(no)), new TestLogger) should equal(true)
    }
  }

  test("continue release blank asks again") {
    val reader = new LineReaderMock(Seq(maybe, yes))
    CloudbeesPlugin.continueRelease(changeLogText, reader, new TestLogger) should equal(true)
    reader.asks should equal(2)
  }
}
