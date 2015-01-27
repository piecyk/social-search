import NativePackagerKeys._

packageArchetype.java_application

name          := """social-search"""

organization  := "com.drwal"

version       := "0.1"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers     ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"       % sprayV,
    "io.spray"            %%  "spray-routing"   % sprayV,
    "io.spray"            %%  "spray-testkit"   % sprayV  % "test",
    "io.spray"            %%  "spray-client"    % sprayV,
    "io.spray"            %%  "spray-json"      % "1.3.1",
    "com.typesafe.akka"   %%  "akka-actor"      % akkaV,
    "com.typesafe.akka"   %%  "akka-slf4j"      % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"    % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"     % "2.3.11" % "test",
    "ch.qos.logback"      %   "logback-classic" % "1.1.2"
  )
}

Revolver.settings
