import net.virtualvoid.sbt.graph.Plugin.graphSettings
import xerial.sbt.Pack._

/*--- Project settings ---*/
name := "api-spray"
 
version := "1.0"

scalaVersion := "2.10.4"

/*--- Repositories ---*/
resolvers ++= Seq(
  "Spray Repo" at "http://repo.spray.io/",
  "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"
)

/*--- Dependencies ---*/
libraryDependencies ++= {
  val akkaVersion = "2.2.4"
  val sprayVersion = "1.2.1"
  val slf4jVersion = "1.7.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "io.spray" % "spray-can" % sprayVersion,
    "io.spray" % "spray-routing" % sprayVersion,
    "org.specs2" %% "specs2" % "1.13",
    "org.json4s" %% "json4s-native" % "3.2.4",
    "org.scalatest" %% "scalatest" % "2.0" % "test",
    "org.slf4j" % "slf4j-log4j12" % slf4jVersion,
    "org.slf4j" % "slf4j-log4j12" % slf4jVersion,
    "org.clapper" %% "grizzled-slf4j" % "1.0.1"
  )
}

/*--- Plugins Settings ---*/
graphSettings

packSettings

packMain := Map("run" -> "pinocchio.api.WebApp")

packGenerateWindowsBatFile := false

packExtraClasspath := Map("run" -> Seq("${PROG_HOME}/conf"))