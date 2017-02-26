name := """play-catan"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
    .settings(javacOptions in Compile += "-parameters")

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  "com.google.guava" % "guava" % "21.0",
  "org.apache.commons" % "commons-lang3" % "3.5",
  "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % "2.8.6",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-guava" % "2.8.6"
)

// Enable Play cache API (based on your Play version) and optionally exclude EhCache implementation
libraryDependencies += play.sbt.PlayImport.cache exclude("net.sf.ehcache", "ehcache-core")
