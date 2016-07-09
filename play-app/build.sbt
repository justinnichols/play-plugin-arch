name := """play-app"""

version := "1.0-SNAPSHOT"

lazy val `play-app` = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-Xmax-classfile-name", "100")

resolvers ++= Seq(
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "commons-io"          % "commons-io"                    % "2.4",
  "akka-task-scheduler" % "akka-task-scheduler_2.11"      % "1.0.0",
  "ro.fortsoft.pf4j"    % "pf4j"                          % "1.0.0",
  "test.plugin"         % "plugin-framework"              % "1.0.0"
)

routesGenerator := InjectedRoutesGenerator

includeFilter in Compile in unmanagedResources := "*"

resourceDirectory in Test <<= baseDirectory apply {(baseDir: File) => baseDir / "testResources"}

LessKeys.sourceMap in Assets := true

LessKeys.compress in Assets := true

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"