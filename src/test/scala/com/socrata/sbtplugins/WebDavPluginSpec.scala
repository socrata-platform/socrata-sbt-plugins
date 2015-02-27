package com.socrata.sbtplugins

import com.googlecode.sardine.SardineFactory
import com.socrata.sbtplugins.WebDavPlugin.MkColException
import org.scalatest.{FunSuiteLike, Matchers}
import sbt._
import StringPath._

class WebDavPluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    WebDavPlugin.trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    WebDavPlugin.requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    WebDavPlugin.projectSettings.isEmpty should equal(false)
  }

  val organization = "org"
  val artifact = "art"
  val version = "ver"
  val scalaVersions = List("1", "2")
  val sbtVersion = "3"
  val expectedPlain = "/org/art/ver"
  test("create paths") {
    val ps = WebDavPlugin.createPaths(organization, artifact, version, scalaVersions, sbtVersion,
      crossPaths = false, mavenStyle = false, isSbtPlugin = false)
    ps should contain(expectedPlain)
  }

  test("create paths for cross build"){
    val ps = WebDavPlugin.createPaths(organization, artifact, version, scalaVersions, sbtVersion,
      crossPaths = true, mavenStyle = false, isSbtPlugin = false)
    ps should contain("/org/art_1/ver")
    ps should contain("/org/art_2/ver")
  }

  test("create paths for plugin"){
    val ps = WebDavPlugin.createPaths(organization, artifact, version, scalaVersions, sbtVersion,
      crossPaths = false, mavenStyle = false, isSbtPlugin = true)
    ps should contain(expectedPlain)
  }

  test("create paths for plugin cross build"){
    val ps = WebDavPlugin.createPaths(organization, artifact, version, scalaVersions, sbtVersion,
      crossPaths = true, mavenStyle = false, isSbtPlugin = true)
    ps should contain("/org/art_1_3/ver")
    ps should contain("/org/art_2_3/ver")
  }

  test("create paths for scala 2.10") {
    val ps = WebDavPlugin.createPaths(organization, artifact, version, List("2.10.XYZ"), sbtVersion,
      crossPaths = true, mavenStyle = false, isSbtPlugin = false)
    ps should contain("/org/art_2.10/ver")
  }

  test("path collections") {
    val ps = WebDavPlugin.pathCollections("/part/of/url")
    ps should equal(List("part", "part/of", "part/of/url"))
  }

  test("get maven repo root") {
    val url = "http://this.is.a.test/dir1/dir2/"
    val root = WebDavPlugin.mavenRoot(
      Some(MavenRepository("maven repo", url)))
    root should equal(Some(url))
  }

  test("get non-maven repo root should do nothing") {
    val root = WebDavPlugin.mavenRoot(
      Some(Resolver.url("ivy repo", new URL("http://this.is.ivy/test/dir1/dir2/"))(Resolver.ivyStylePatterns)))
    root should equal(None)
  }

  test("publish to urls maven") {
    val us = WebDavPlugin.publishToUrls(List("path1", "path2"), Some(MavenRepository("test", "http://test/")))
    us.get should contain("http://test/path1")
    us.get should contain("http://test/path2")
  }

  test("publish to urls non-maven should do nothing") {
    val us = WebDavPlugin.publishToUrls(List("path3", "path4"), Some(Resolver.defaultLocal))
    us should equal(None)
  }

  test("exists true") {
    val sardine = SardineFactory.begin()
    val t = WebDavPlugin.exists(sardine, "http://www.googlecode.com/")
    t should equal(Right(true))
  }

  test("exists false") {
    val sardine = SardineFactory.begin()
    val t = WebDavPlugin.exists(sardine, "http://www.googlecode.com/notexist")
    t should equal(Right(false))
  }

  test("exists throws") {
    val sardine = SardineFactory.begin()
    val t = WebDavPlugin.exists(sardine, "thisisnotavalidurl")
    t match {
      case Left(e: Exception) => () // success
      case _ => fail("should have returned a throwable")
    }
  }

  val urlRoot = "root"
  test("mkcol") {
    val logger = new TestLogger
    val paths = List("5", "6")
    val sardine = new SardineMock
    sardine.urls += urlRoot -> true

    paths.foreach(p => sardine.exists(urlRoot / p) should equal(false))

    WebDavPlugin.mkcol(sardine, urlRoot, paths, logger)

    paths.foreach(p => sardine.exists(urlRoot / p) should equal(true))
    logger.lastMessage should startWith("WebDav: Creating collection")
  }

  test("mkcol root doesn't exist should throw") {
    val sardine = SardineFactory.begin()
    a[MkColException] should be thrownBy {
      WebDavPlugin.mkcol(sardine, "http://www.googlecode.com/anotherworld", List("7", "8"), new TestLogger)
    }
  }

  test("mkcol short circuit on existing path") {
    val path = "9"
    val logger = new TestLogger
    val paths = List(path, path)
    val sardine = new SardineMock
    sardine.urls += urlRoot -> true

    WebDavPlugin.mkcol(sardine, urlRoot, paths, logger)

    logger.lastMessage should startWith("WebDav: Found collection")
  }

  val http: String = "http://"
  val realm: String = "realm"
  val user: String = "user"
  val pass: String = "pass"
  test("get credentials") {
    val host = "host"
    val hostUrl = http / host
    val cred = new DirectCredentials(realm, host, user, pass)
    val cs = WebDavPlugin.getCredentialsForHostOrElse(Some(MavenRepository(host, hostUrl)), List(cred), new TestLogger)
    cs should equal(cred)
  }

  test("get credentials no matches") {
    val host = "foo"
    val hostUrl = http / "bar"
    val cred = new DirectCredentials(realm, host, user, pass)
    a[MkColException] should be thrownBy {
      val cs = WebDavPlugin.getCredentialsForHostOrElse(Some(MavenRepository("a", hostUrl)), List(cred), new TestLogger)
    }
  }

  test("make collections") {
    val host = "b"
    val hostUrl = http / host
    val sardine = new SardineMock
    sardine.urls += hostUrl -> true

    WebDavPlugin.makeCollections(Some(MavenRepository(host, hostUrl)),
    Seq(List("10", "11")), new DirectCredentials(realm, host, user, pass),
      (user, pass) => sardine, new TestLogger)
  }
}
