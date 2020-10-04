name := "rop"
organization in ThisBuild := "com.malagi.srihari"
version in ThisBuild:= "0.1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.12"

lazy val root = (project in file("."))
  .settings(
    commonSettings,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % Test,
  )

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)
