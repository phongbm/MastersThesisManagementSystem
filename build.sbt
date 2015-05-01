name := """MastersThesisManagementSystem"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.3-1100-jdbc41",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "com.typesafe.play" %% "play-mailer" % "2.4.0-RC1",
  filters
)

libraryDependencies += javaEbean