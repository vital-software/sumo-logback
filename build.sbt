organization := "com.github.vital-software"

name := "sumo-logback"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "com.sumologic.plugins.log4j" % "sumologic-log4j2-appender" % "1.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.8.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "ch.qos.logback" % "logback-core" % "1.2.3",
  "org.specs2" %% "specs2" % "2.3.13" % Test
)

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation")

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

sonatypeProfileName := "com.github.vital-software"

pomExtra := (
  <url>https://github.com/vital-software/sumo-logback</url>
    <licenses>
      <license>
        <name>MIT</name>
        <url>http://opensource.org/licenses/MIT</url>
        <distribution>repo</distribution>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:vital-software/sumo-logback.git</url>
      <connection>scm:git:git@github.com:vital-software/sumo-logback.git</connection>
    </scm>
    <developers>
      <developer>
        <id>apatzer</id>
        <name>Aaron Patzer</name>
        <url>https://github.com/apatzer</url>
      </developer>
    </developers>)
