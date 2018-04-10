import Dependencies._
import CompilerOptions._

lazy val mainScalaVersion = "2.12.4"
lazy val orgName = "ly.analogical"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

def buildSettings = inThisBuild(
  Seq(
    organization := orgName,
    scalaVersion := mainScalaVersion
  )
)

lazy val lintingSettings = Seq(
  wartremoverErrors in (Compile, compile) ++= Warts.allBut(Wart.Var, Wart.ImplicitParameter, Wart.DefaultArguments),
  scalastyleFailOnWarning := true
)

lazy val testSettings = Seq(
  javaOptions in Test += "-Xss64m",
  fork in Test := true
)

lazy val coverageSettings = Seq(
  coverageEnabled.in(Test, test) := true,
  coverageMinimum := 100,
  coverageFailOnMinimum := true
)

lazy val baseSettings =
  testSettings ++
  CompilerOptions.flags ++
  buildSettings

lazy val mainAndTest = "compile->compile;test->test"

lazy val root = (project in file("."))
  .settings(baseSettings)
  .settings(
    name := "scaleda",
    description := "Exploratory Data Analysis in Scala",
  )
  .aggregate(common, datasource)

lazy val common = (project in file("scaleda-common"))
  .settings(baseSettings)
  .settings(coverageSettings)
  .settings(
    name := "scaleda-common",
    description := "Commons/utils for scaleda",
    libraryDependencies ++= commonDependencies
  )

lazy val datasource = (project in file("scaleda-datasource"))
  .settings(baseSettings)
  .settings(coverageSettings)
  .settings(
    name := "scaleda-datasource",
    description := "Datasource interaction for scaleda",
    libraryDependencies ++= datasourceDependencies,
    version := "0.0.1-SNAPSHOT"
  )
  .dependsOn(common % mainAndTest)
