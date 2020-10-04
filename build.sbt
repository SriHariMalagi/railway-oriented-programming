name := """rop"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.12.12"

scalaSource in Test := baseDirectory.value / "test"
scalaSource := baseDirectory.value / "src"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"