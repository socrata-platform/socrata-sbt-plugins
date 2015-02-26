package com.socrata.sbtplugins

import com.socrata.sbtplugins.StringPath._
import org.scalatest.{Matchers, FunSuiteLike}

class StringPathSpec extends FunSuiteLike with Matchers {
  val expected =  "/this/is/a/test/"

  test("implicit conversion from string") {
    val sp: StringPath = expected
    sp.path should equal(expected)
  }

  test("path concatenation") {
    val concat = "/this/" /  "/is/" / "/a/" / "/test/"
    concat should equal(expected)

    val concat1 = "this" / "is" / "a" / "test"
    concat1 should equal(expected.substring(1, expected.length-1))

    val concat2 = "this/" / "is/" / "a/" / "test/"
    concat2 should equal(expected.substring(1, expected.length))

    val concat3 = "/this" / "/is" / "/a" / "/test"
    concat3 should equal(expected.substring(0, expected.length-1))
  }
}
