import scoverage.ScoverageSbtPlugin.ScoverageKeys._

name := "scala-juman-knp"
version := "0.0.1"
organization := "en-japan"

scalaVersion := "2.11.7"
scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Ywarn-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-inaccessible",
    "-Ywarn-infer-any",
    "-Ywarn-nullary-override",
    "-Ywarn-nullary-unit",
    "-Ywarn-numeric-widen",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-Ywarn-value-discard",
    "-target:jvm-1.7",
    "-encoding", "UTF-8"
  )

libraryDependencies += "org.typelevel" %% "cats" % "0.4.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.6" % "test"


coverageExcludedPackages := "com.enjapan.knp.KNP"
