name := "sample"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.0",
  "com.typesafe.play" % "play-json_2.11" % "2.4.2",
  "io.megl" % "play-json-extra_2.11" % "2.4.3",
  "com.typesafe.play" %% "play-ws" % "2.4.3",
  "org.json4s" %% "json4s-jackson" % "3.3.0"
)