package com.socrata.sbtplugins

import com.socrata.sbtplugins.StringPath._
import org.scalatest.{ShouldMatchers, FunSuiteLike}

class StringPathSpec extends FunSuiteLike with ShouldMatchers {
  test("implicit conversion from string") {
    val sp: StringPath = "/this/is/a/test/"
    sp.path should equal("/this/is/a/test/")
  }

  test("path concatenation") {
    val concat: String = "/this/" /  "/is/" / "/a/" / "/test/"
    concat should equal("/this/is/a/test/")
  }
}
