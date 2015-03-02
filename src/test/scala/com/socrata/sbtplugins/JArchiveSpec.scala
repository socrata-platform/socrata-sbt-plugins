package com.socrata.sbtplugins

import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class JArchiveSpec extends FunSuiteLike with Matchers {
  val resources = "file:./src/test/resources"
  val dummyFileName = "DummyFile.txt"
  val targetPath = "target"
  val target = file(targetPath) / dummyFileName
  val dummyFileCreated = s"created: $target"

  test("get file from file system") {
    val logger = new TestLogger
    val f = new JArchive(new URL(s"$resources/$dummyFileName")).getFileFromJar(target, logger)
    logger.lastMessage should equal(dummyFileCreated)
  }

  test("get file from jar") {
    val logger = new TestLogger
    val jarPath = s"jar:$resources/dummy.jar!/$dummyFileName"
    val f = new JArchive(new URL(jarPath)).getFileFromJar(target, logger)
    logger.lastMessage should equal(dummyFileCreated)
  }

  test("get file from http shows specific error") {
    val logger = new TestLogger
    val f = new JArchive(new URL("http://www.googlecode.com/")).getFileFromJar(file("sardine"), logger)
    logger.lastMessage should equal("http connection type not implemented")
  }

  test("get file from unknown shows generic error") {
    val logger = new TestLogger
    val f = new JArchive(new URL("ftp://www.piratebay.com/")).getFileFromJar(file("300"), logger)
    logger.lastMessage should startWith("unknown connection type")
  }

  test("get file from nonexisting jar shows io error") {
    val logger = new TestLogger
    val jarPath = s"jar:$resources/smarty.jar!/$dummyFileName"
    val f = new JArchive(new URL(jarPath)).getFileFromJar(target, logger)
    logger.lastMessage should endWith("(No such file or directory)")
  }
}
