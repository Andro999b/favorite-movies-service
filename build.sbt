name := """movie-favorites-lists"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.9.1"
libraryDependencies += guice
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
libraryDependencies += "io.swagger" % "swagger-play2_2.12" % "1.6.0"

libraryDependencies += "org.hamcrest" % "hamcrest-all" % "1.3" % "test"
