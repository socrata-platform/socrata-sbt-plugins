package coverageTest

import org.scalatest._

class SimpleTest extends FunSuite with MustMatchers {
  test("fibonacci sequence") {
    val fib = Simple.fibonacci
    fib mustEqual List(1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89) //scalastyle:ignore
  }
}
