
name := "akka"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
"com.typesafe.akka" %% "akka-actor" % "2.5.19",
"com.typesafe.akka" %% "akka-cluster" % "2.5.19"
)

libraryDependencies += "com.typesafe.akka" %% "akka-cluster-metrics" % "2.5.19"
