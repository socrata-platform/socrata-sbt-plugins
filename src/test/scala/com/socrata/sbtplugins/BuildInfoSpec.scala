package com.socrata.sbtplugins

import org.scalatest.{FunSuiteLike, Matchers}

class BuildInfoSpec extends FunSuiteLike with Matchers {
  test("BuildInfo includes these fields") {
    Option(BuildInfo.name) should be('defined)
    Option(BuildInfo.version) should be('defined)
    Option(BuildInfo.scalaVersion) should be('defined)
    Option(BuildInfo.sbtVersion) should be('defined)
    Option(BuildInfo.buildTime) should be('defined)
    Option(BuildInfo.revision) should be('defined)
  }

  test("BuildInfo json") {
    Option(BuildInfo.toJson) should be('defined)
  }
}
