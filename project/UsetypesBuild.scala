import sbt._
import sbt.Keys._
import Dependencies._

object UsetypesBuild extends Build {

    lazy val root = Project(id = "usetypes", base = file("."),
        settings = Project.defaultSettings ++ Seq(
	        name := "usetypes",
	        version := "0.1-SNAPSHOT",

	        description := "a DDD demo in scala",

	        version := "0.1-SNAPSHOT",

	        scalaVersion := "2.10.3",

	        libraryDependencies ++= scalaLibs ++ javaLibs ++ tests
           ))
}

object Dependencies {
	val scalaLibs = Seq(
		"org.scalaz"                %% "scalaz-core"            % "7.0.6"
	)

	val javaLibs = Seq(

	)

	val tests = Seq(
		"org.scalatest"             %% "scalatest"              % "2.1.0"       % "test"
	)
}