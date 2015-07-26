package com.socrata.sbtplugins

import com.socrata.sbtplugins.CloudbeesPlugin._
import org.scalatest.{FunSuiteLike, Matchers}
import sbt._
import sbtrelease.{Git, Mercurial, ReleasePlugin, Subversion}

class CloudbeesPluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    requires should equal(plugins.JvmPlugin && MimaPlugin && ReleasePlugin && WebDavPlugin)
  }

  test("has project settings") {
    projectSettings.isEmpty should equal(false)
  }

  val dotfile: File = file(".")
  test("get vcs git") {
    val git = gitOrHalt(Some(new Git(dotfile)))
  }

  test("get vcs mercurial throws") {
    a[RuntimeException] should be thrownBy {
      val merc = gitOrHalt(Some(new Mercurial(dotfile)))
    }
  }

  test("get vcs subversion throws") {
    a[RuntimeException] should be thrownBy {
      val svn = gitOrHalt(Some(new Subversion(dotfile)))
    }
  }

  test("git last release tag search command") {
    val git = new GitMock(dotfile)
    val tag = lastReleaseTag(git)
    tag should equal("describe --tags --abbrev=0 --match=v[0-9]*")
  }

  val lastReleaseText = "v42"
  test("git log") {
    val git = new GitMock(dotfile)
    val tag = gitLog(git, lastReleaseText)
    tag should equal("log v42..HEAD --pretty=format:%h %<(20)%an %ci %s")
  }

  val changeLogText =
 """
   |** Changes since release v42: **
   |this is a new commit
 """.stripMargin
  test("change log") {
    val cl = changeLog(lastReleaseText, "this is a new commit", new TestLogger)
    cl should equal(changeLogText)
  }

  val yes = "y"
  val no = "n"
  val maybe = " "
  test("continue release yes") {
    continueRelease(changeLogText, new LineReaderMock(Seq(yes)), new TestLogger, None) should equal(true)
  }

  test("continue release no") {
    a[RuntimeException] should be thrownBy {
      continueRelease(changeLogText, new LineReaderMock(Seq(no)), new TestLogger, None) should equal(true)
    }
  }

  test("continue release blank asks again") {
    val reader = new LineReaderMock(Seq(maybe, yes))
    continueRelease(changeLogText, reader, new TestLogger, None) should equal(true)
    reader.asks should equal(2)
  }
}
