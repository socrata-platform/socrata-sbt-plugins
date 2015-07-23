package com.socrata.sbtplugins

import com.socrata.sbtplugins.StylePlugin._
import org.scalastyle._
import org.scalastyle.scalariform.FieldNamesChecker
import org.scalatest.{FunSuiteLike, Matchers}
import _root_.sbt.{Level => _, _}

class StylePluginSpec extends CheckerTest {
  test("triggers on all requirements") {
    trigger should equal(AllRequirements)
  }

  test("depends on jvm plugin") {
    requires should equal(plugins.JvmPlugin)
  }

  test("has project settings") {
    projectSettings.isEmpty should equal(false)
  }

  val classUnderTest = classOf[FieldNamesChecker]
  val keyUnderTest = "field.name"
  test("scalastyle - allow case class unapply") {
    val source =
      """
        |package foobar
        |
        |class CaseClassUnapply {
        |  val Foo(bar) = Foo("42")
        |  val (a: Int, b: Int) = (3, 5)
        |}
        |
        |case class Foo(bar: String)
      """.stripMargin

    val originalRegex = "^[a-z][A-Za-z0-9]*$"
    assertErrors(List(
      StyleError(null, classUnderTest, keyUnderTest, Level("warning"), List(originalRegex), Some(5), Some(6), None),
      StyleError(null, classUnderTest, keyUnderTest, Level("warning"), List(originalRegex), Some(6), Some(6), None)
    ), source)

    val awfulRegex = "^[A-Za-z0-9(]+$"
    assertErrors(Nil, source, params = Map("regex" -> awfulRegex))
  }
}

// partial pasta from https://github.com/scalastyle/scalastyle/blob/master/src/test/scala/org/scalastyle/file/CheckerTest.scala
trait CheckerTest extends FunSuiteLike with Matchers {
  protected val classUnderTest: Class[_ <: Checker[_]]

  object NullFileSpec extends FileSpec {
    def name: String = ""
  }

  protected def assertErrors[T <: FileSpec](expected: List[Message[T]], source: String, params: Map[String, String] = Map(),
                                            customMessage: Option[String] = None, commentFilter: Boolean = true, customId: Option[String] = None) = {
    val classes =  List(ConfigurationChecker(classUnderTest.getName, WarningLevel, enabled = true, params, customMessage, customId))
    val configuration = ScalastyleConfiguration("", commentFilter, classes)

    val expectedStr = expected.mkString("\n")
    val actualStr = new CheckerUtils().verifySource(configuration, classes, NullFileSpec, source).mkString("\n")
    expectedStr should be (actualStr)
  }
}
