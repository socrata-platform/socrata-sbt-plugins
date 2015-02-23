import org.scalatest._

class FailSpec extends FlatSpec with Matchers {

  "An intentional fail" should "fail when asked nicely" in {
    0 should be (1)
  }

}
