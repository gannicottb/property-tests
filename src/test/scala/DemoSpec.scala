import zio.random.Random
import zio.test._
import zio.test.Assertion._
import zio.test.magnolia._

object DemoSpec extends DefaultRunnableSpec {
  def spec =
    suite("demo")(
      testM("test string concat") {
        /* Code under test */
        def concat(a: String, b: String, c: String) = s"$a$b$c"

        /* Properties */
        def containsAllStringsInOrder(a: String,
                                      b: String,
                                      c: String): Assertion[String] =
          startsWithString(a) && containsString(b) && endsWithString(c)

        def hasSameLengthAsCombinedInput(a: String,
                                         b: String,
                                         c: String): Assertion[String] =
          hasSizeString(equalTo(a.length + b.length + c.length))

        /* Check properties */
        check(Gen.vectorOfN(3)(Gen.anyString)) {
          case Vector(a, b, c) =>
            val result = concat(a, b, c)
            assert(result)(
              containsAllStringsInOrder(a, b, c) &&
                hasSameLengthAsCombinedInput(a, b, c)
            )
        }
      },
      testM("map") {
        /*
          The function we want to test. Contrived, I know :)
         */
        def barChecker(s: String): Boolean = s.startsWith("bar-")

        /*
          The generator that makes appropriate input
         */
        val specialStrings: Gen[Random with Sized, String] =
          Gen.anyString.map(s => s"bar-$s")

        /*
          Check that a property holds
         */
        check(specialStrings) { s =>
          assert(barChecker(s))(isTrue)
        }
      },
      testM("cross and test oracle") {
        /* Code under test */
        def repeater(s: String, i: Int): String = s * i

        /* Generator */
        val stringAndInt: Gen[Random with Sized, (String, Int)] =
          Gen.anyString cross Gen.int(0, 1000)

        /* Properties */
        def equalToReferenceImplementation(s: String,
                                           i: Int): Assertion[String] = {
          equalTo(Vector.fill(i)(s).mkString)
        }

        /* Check properties */
        check(stringAndInt) {
          case (s, i) =>
            assert(repeater(s, i))(
              hasSizeString(equalTo(s.length * i)) &&
                equalToReferenceImplementation(s, i)
            )
        }
      },
      testM("round tripping - int to string to int") {
        check(Gen.anyInt) { i =>
          assert(i)(equalTo(i.toString.toInt))
        }
      },
      testM("magnolia") {
        /* Code under test */
        case class Name(first: String, last: String) {
          override def toString: String = s"$first $last"
        }

        /* Generator of our custom case class */
        val genName: Gen[Random with Sized, Name] = DeriveGen[Name]

        /* Check that toString prints the names in order! */
        check(genName) { name =>
          assert(name.toString)(
            startsWithString(name.first) && endsWithString(name.last)
          )
        }

      }
    )
}
