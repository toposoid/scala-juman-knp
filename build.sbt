import scoverage.ScoverageSbtPlugin.ScoverageKeys._

name := "scala-juman-knp"
description := "Scala wrapper around JUMAN and KNP."
version := "0.0.2"
organization := "com.enjapan"

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

bintrayOrganization := Some("en-japan")
bintrayRepository := "maven-oss"
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
bintrayVcsUrl := Some("https://github.com/en-japan/scala-juman-knp.git")
bintrayPackageLabels := Seq("scala", "knp", "juman")
bintrayPackageAttributes ~= (_ ++ Map(
  "issue_tracker_url" -> Seq(bintry.Attr.String("https://github.com/en-japan/scala-juman-knp/issues")),
  "github_repo" -> Seq(bintry.Attr.String("en-japan/scala-juman-knp"))
))

