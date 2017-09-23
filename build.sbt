name := """movie-favorites-lists"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies += guice
libraryDependencies += ws
libraryDependencies ++= Seq(
  javaJdbc,
  "com.h2database" % "h2" % "1.4.+"
)
libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-core" % "5.2.+",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-hibernate5" % "2.9.+"
)
libraryDependencies += "com.theoryinpractise" % "halbuilder5" % "5.+"

libraryDependencies += "org.hamcrest" % "hamcrest-all" % "1.3" % "test"