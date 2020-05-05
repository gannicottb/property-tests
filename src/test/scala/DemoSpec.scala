import zio.random.Random
import zio.test._
import zio.test.Assertion._

object DemoSpec extends DefaultRunnableSpec {
  def spec =
    suite("demo")(
      testM("test string concat") {
        def concat(a: String, b: String, c: String) = s"$a$b$c"

        check(Gen.anyString, Gen.anyString, Gen.anyString) {
          case (a, b, c) =>
            val result = concat(a, b, c)
            assert(result)(
              startsWithString(a) && containsString(b) && endsWithString(c) && hasSizeString(
                equalTo(a.length + b.length + c.length)
              )
            )
        }
      },
      testM("map") {
        def barChecker(s: String): Boolean = s.startsWith("bar-")
        val specialStrings = Gen.anyString.map(s => s"bar-$s")
        check(specialStrings) { s =>
          assert(barChecker(s))(isTrue)
        }
      },
      testM("cross") {
        def repeater(s: String, times: Int) = s * times
        val stringAndInt = Gen.anyString cross Gen.int(0, 1000)

        check(stringAndInt) {
          case (s, i) =>
            assert(repeater(s, i))(hasSizeString(equalTo(s.length * i)))
        }
      },
      testM("round tripping - int to string to int") {
        check(Gen.anyInt) { i =>
          assert(i)(equalTo(i.toString.toInt))
        }
      }
    )
}
