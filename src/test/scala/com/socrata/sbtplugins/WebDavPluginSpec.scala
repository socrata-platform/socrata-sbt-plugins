package com.socrata.sbtplugins

import com.googlecode.sardine.SardineFactory
import com.socrata.sbtplugins.StringPath._
import com.socrata.sbtplugins.WebDavPlugin._
import org.scalatest.{FunSuiteLike, Matchers}
import sbt._

class WebDavPluginSpec extends FunSuiteLike with Matchers {
  test("triggers on all requirements") {
    trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    projectSettings.isEmpty should equal(false)
  }

  val organization = "org"
  val artifact = "art"
  val version = "ver"
  val scalaVersions = List("1", "2")
  val sbtVersion = "3"
  val expectedPlain = "/org/art/ver"
  test("create paths") {
    val ps = createPaths(organization, artifact, version, scalaVersions, sbtVersion,
      crossPaths = false, mavenStyle = false, isSbtPlugin = false)
    ps should contain(expectedPlain)
  }

  test("create paths for cross build"){
    val ps = createPaths(organization, artifact, version, scalaVersions, sbtVersion,
      crossPaths = true, mavenStyle = false, isSbtPlugin = false)
    ps should contain("/org/art_1/ver")
    ps should contain("/org/art_2/ver")
  }

  test("create paths for plugin"){
    val ps = createPaths(organization, artifact, version, scalaVersions, sbtVersion,
      crossPaths = false, mavenStyle = false, isSbtPlugin = true)
    ps should contain(expectedPlain)
  }

  test("create paths for plugin cross build"){
    val ps = createPaths(organization, artifact, version, scalaVersions, sbtVersion,
      crossPaths = true, mavenStyle = false, isSbtPlugin = true)
    ps should contain("/org/art_1_3/ver")
    ps should contain("/org/art_2_3/ver")
  }

  test("create paths for scala 2.10") {
    val ps = createPaths(organization, artifact, version, List("2.10.XYZ"), sbtVersion,
      crossPaths = true, mavenStyle = false, isSbtPlugin = false)
    ps should contain("/org/art_2.10/ver")
  }

  test("path collections") {
    val ps = pathCollections("/part/of/url")
    ps should equal(List("part", "part/of", "part/of/url"))
  }

  test("get maven repo root") {
    val url = "http://this.is.a.test/dir1/dir2/"
    val root = mavenRoot(
      Some(MavenRepository("maven repo", url)))
    root should equal(Some(url))
  }

  test("get non-maven repo root should do nothing") {
    val root = mavenRoot(
      Some(Resolver.url("ivy repo", new URL("http://this.is.ivy/test/dir1/dir2/"))(Resolver.ivyStylePatterns)))
    root should equal(None)
  }

  test("publish to urls maven") {
    val us = publishToUrls(List("path1", "path2"), Some(MavenRepository("test", "http://test/")))
      .getOrElse(fail("publishToUrls was empty"))
    us should contain("http://test/path1")
    us should contain("http://test/path2")
  }

  test("publish to urls non-maven should do nothing") {
    val us = publishToUrls(List("path3", "path4"), Some(Resolver.defaultLocal))
    us should equal(None)
  }

  val urlRoot = "root"
  test("mkcol") {
    val logger = new TestLogger
    val paths = List("5", "6")
    val sardine = new SardineMock
    sardine.urls += urlRoot -> Right(true)

    paths.foreach(p => sardine.exists(urlRoot / p) should equal(false))

    mkcol(sardine, urlRoot, paths, logger)

    paths.foreach(p => sardine.exists(urlRoot / p) should equal(true))
    logger.lastMessage should startWith("WebDav: Creating collection")
  }

  test("mkcol exception looking for root should rethrow") {
    val root = "http://whatever"
    val sardine = new SardineMock

    sardine.urls += root -> Left(new RuntimeException("root exists exception"))

    a[MkColException] shouldBe thrownBy {
      mkcol(sardine, root, List("12"), new TestLogger)
    }
  }

  test("mkcol root doesn't exist should throw") {
    val sardine = SardineFactory.begin()
    a[MkColException] should be thrownBy {
      mkcol(sardine, "http://www.googlecode.com/anotherworld", List("7", "8"), new TestLogger)
    }
  }

  test("mkcol short circuit on existing path") {
    val path = "9"
    val logger = new TestLogger
    val paths = List(path, path)
    val sardine = new SardineMock
    sardine.urls += urlRoot -> Right(true)

    mkcol(sardine, urlRoot, paths, logger)

    logger.lastMessage should startWith("WebDav: Found collection")
  }

  test("mkcol exception looking for subpath should rethrow") {
    val path = "13"
    val sardine = new SardineMock
    sardine.urls += urlRoot -> Right(true)
    sardine.urls += urlRoot / path -> Left(new RuntimeException("subpath exists exception"))

    a[MkColException] shouldBe thrownBy {
      mkcol(sardine, urlRoot, List(path), new TestLogger)
    }
  }

  val http: String = "http://"
  val realm: String = "realm"
  val user: String = "user"
  val pass: String = "pass"
  test("get credentials") {
    val host = "host"
    val hostUrl = http / host
    val cred = new DirectCredentials(realm, host, user, pass)
    val cs = getCredentialsForHostOrElse(Some(MavenRepository(host, hostUrl)), List(cred), new TestLogger)
    cs should equal(cred)
  }

  test("get credentials no matches") {
    val host = "foo"
    val hostUrl = http / "bar"
    val cred = new DirectCredentials(realm, host, user, pass)
    a[MkColException] should be thrownBy {
      val cs = getCredentialsForHostOrElse(Some(MavenRepository("a", hostUrl)), List(cred), new TestLogger)
    }
  }

  test("make collections") {
    val host = "b"
    val hostUrl = http / host
    val sardine = new SardineMock
    sardine.urls += hostUrl -> Right(true)

    makeCollections(Some(MavenRepository(host, hostUrl)),
    Seq(List("10", "11")), new DirectCredentials(realm, host, user, pass),
      (user, pass) => sardine, new TestLogger)
  }

  test("mkcolAction") {
    val host = "c"
    val hostUrl = http / host
    val sardine = new SardineMock
    sardine.urls += hostUrl -> Right(true)

    mkcolAction(organization, artifact, version, scalaVersions, sbtVersion, crossPaths = true,
      Some(MavenRepository(host, hostUrl)), List(new DirectCredentials(realm, host, user, pass)),
      (u,p) => sardine, new TestLogger, mavenStyle = true, sbtPlugin = true)
  }
}
