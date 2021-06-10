ThisBuild / scalaVersion := "3.0.0"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.indoorvivants"

libraryDependencies += "org.scalafx"   %% "scalafx"     % "16.0.0-R24"
libraryDependencies += "org.typelevel" %% "cats-effect" % "3.1.1"
libraryDependencies += "co.fs2"        %% "fs2-core"    % "3.0.4"

// Determine OS version of JavaFX binaries
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _                            => throw new Exception("Unknown platform!")
}

fork := true

scalacOptions += "-source:future"
scalacOptions += "-language:adhocExtensions"

// Add dependency on JavaFX libraries, OS dependent
lazy val javaFXModules =
  Seq("base", "controls", "graphics", "web")
libraryDependencies ++= javaFXModules.map(m =>
  "org.openjfx" % s"javafx-$m" % "16" classifier osName
)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
