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
            assert(concat(a, b, c))(
              containsString(a) && containsString(b) && containsString(c)
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
        // Turns ("foo", 0) into "fff"
        def replacer(s: String, index: Int): String = {
          Vector.fill(s.length)(s.charAt(index)).mkString
        }

        val stringAndIndex = Gen.anyString
          .cross(Gen.anyInt)
          .map {
            case (s, i) if s.nonEmpty => (s, i.abs % s.length)
            case _                    => ("a", 0) // we aren't testing with empty strings here
          }

        check(stringAndIndex) {
          case (s, i) =>
            val result = replacer(s, i)
            assert(result.length)(equalTo(s.length)) && assert(result)(
              equalTo(s.charAt(i).toString * s.length)
            )
        }

      }
    )
}
