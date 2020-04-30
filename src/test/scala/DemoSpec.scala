import zio.test._
import zio.test.Assertion._

object DemoSpec extends DefaultRunnableSpec {
  def spec =
    suite("demo")(
      testM("test add") {
        def add(a: Int, b: Int): Int = a + b

        val anyPositiveInt = Gen.int(0, Int.MaxValue / 2) // avoid overflow wrapping (maxValue + maxValue = -2)
        check(anyPositiveInt.zip(anyPositiveInt)) {
          case (a, b) => {
            val result = add(a, b)
            assert(result)(isGreaterThan(a) && isGreaterThan(b))
          }
        }
      },
      testM("test string concat") {
        def concat(a: String, b: String, c: String) = s"$a$b$c"

        val anyThreeStrings = Gen.vectorOfN(3)(Gen.anyString)
        check(anyThreeStrings) {
          case Vector(a, b, c) =>
            assert(concat(a, b, c))(
              containsString(a) && containsString(b) && containsString(c)
            )
        }
      }
    )
}
