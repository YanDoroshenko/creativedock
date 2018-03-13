name := "creativedock"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

val http4sVersion = "0.18.2"

libraryDependencies ++=
  "org.apache.kafka" %% "kafka" % "1.0.1" ::
    "net.cakesolutions" %% "scala-kafka-client" % "1.0.0" ::
    "org.http4s" %% "http4s-blaze-server" % http4sVersion ::
    "org.http4s" %% "http4s-circe" % http4sVersion ::
    "org.http4s" %% "http4s-dsl" % http4sVersion ::
    "io.circe" %% "circe-generic" % "0.9.1" ::
    "io.circe" %% "circe-literal" % "0.9.1" ::
    "org.scalatest" %% "scalatest" % "3.0.5" % "test" ::
    Nil