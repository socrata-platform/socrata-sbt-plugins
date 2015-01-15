package coverageTest

import org.scalatest._

class SimpleTest extends FunSuite with MustMatchers {
  test("fibonacci sequence") {
    val fib = Simple.fibonacci(9)
    fib mustEqual 55
  }
}
