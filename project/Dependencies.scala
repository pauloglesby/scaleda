import sbt._

object Dependencies {

  lazy val catsVersion = "1.0.1"
  lazy val shapelessVersion = "2.3.2"

  lazy val commonMainDependencies = Seq(
    "org.typelevel" %% "cats-core" % catsVersion
  )

  lazy val commonTestDependencies = Seq(
    "org.scalatest" %% "scalatest" % "3.0.4",
    "org.scalacheck" %% "scalacheck" % "1.13.5"
  ).map(_ % "test")

  lazy val parboiled2 = "org.parboiled" %% "parboiled" % "2.1.4" excludeAll ExclusionRule(organization = "com.chuusai")

  lazy val shapeless = "com.chuusai" %% "shapeless" % shapelessVersion

  lazy val commonDependencies = commonMainDependencies ++ commonTestDependencies

  // TODO remove parboiled2 from here?
  lazy val datasourceDependencies = commonDependencies ++ Seq(parboiled2, shapeless)

  lazy val stataDependencies = commonDependencies ++ Seq(parboiled2, shapeless)

}

