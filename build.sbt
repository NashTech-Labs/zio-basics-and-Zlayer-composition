ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val root = (project in file("."))
  .settings(
    name := "Zio-Zlayers"
  )
libraryDependencies += "dev.zio" %% "zio" % "1.0.12"
