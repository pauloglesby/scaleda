import sbt._

object Dependencies {

  lazy val catsVersion = "1.0.1"

  lazy val commonMainDependencies = Seq(
    "org.typelevel" %% "cats-core" % catsVersion
  )

  lazy val commonTestDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.4"
  ).map(_ % "test")

  lazy val datasourceDependencies = commonMainDependencies ++ commonTestDependencies
  lazy val commonDependencies = commonMainDependencies ++ commonTestDependencies

}

