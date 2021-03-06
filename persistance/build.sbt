
name := "akka"

version := "1.0"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
"com.typesafe.akka" %% "akka-actor" % "2.5.19",
"com.typesafe.akka" %% "akka-persistence" % "2.5.19",
"org.iq80.leveldb"            % "leveldb"          % "0.7",
"org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8"
)
