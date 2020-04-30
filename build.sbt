name := "property-tests"

version := "0.1"

scalaVersion := "2.12.10"

lazy val ZIOVersion = "1.0.0-RC18-2"
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % ZIOVersion,
  "dev.zio" %% "zio-test" % ZIOVersion % "test",
  "dev.zio" %% "zio-test-sbt" % ZIOVersion % "test",
  "dev.zio" %% "zio-test-magnolia" % ZIOVersion % "test"
)
testFrameworks ++= Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
