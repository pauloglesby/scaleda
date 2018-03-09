import Dependencies._
import CompilerOptions._

lazy val mainScalaVersion = "2.11.8"
lazy val orgName = "ly.analogical"

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

lazy val commonSettings =
  testSettings ++
  CompilerOptions.flags ++ 
  buildSettings

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "scaleda",
    description := "Exploratory Data Analysis in Scala",
  )
  .aggregate(datasource)

lazy val datasource = (project in file("scaleda-datasource"))
  .settings(commonSettings)
  .settings(
    name := "scaleda-datasource",
    description := "Datasource interaction for scaleda",
    libraryDependencies ++= datasourceDependencies,
    version := "0.0.1-SNAPSHOT",
    coverageMinimum := 100,
    coverageFailOnMinimum := true
  )
