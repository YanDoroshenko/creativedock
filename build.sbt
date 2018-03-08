name := "creativedock"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

libraryDependencies ++=
  "org.apache.kafka" %% "kafka" % "1.0.1" ::
    "net.cakesolutions" %% "scala-kafka-client" % "1.0.0" ::
    "org.http4s" %% "http4s-blaze-server" % "0.18.1" ::
    "org.http4s" %% "http4s-circe" % "0.18.1" ::
    "org.http4s" %% "http4s-dsl" % "0.18.1" ::
    Nil