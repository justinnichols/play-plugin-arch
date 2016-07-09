name := """akka-task-scheduler"""

version := "1.0.0"

lazy val `akka-task-scheduler` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.12" % "test"
)

publishMavenStyle := true