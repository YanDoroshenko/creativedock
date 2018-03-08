package com.github.yandoroshenko.creativedock


import cats.effect.IO
import fs2.StreamApp
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, Future}

object RestController extends StreamApp[IO] with Http4sDsl[IO] {

  val service: HttpService[IO] = HttpService[IO] {
    case GET -> Root / "groups" / name / "info" =>
      Await.result(
        Producer.send(Info(), "name", name)
          .flatMap(_ => {
            InfoConsumer.getInfo(name) match {
              case Some(f) => f
              case _ => Future(None)
            }
          })
          .map {
            case Some(r) => Ok(r.value())
            case _ => NotFound()
          }, Duration(1000, MILLISECONDS))
  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] =
    BlazeBuilder[IO]
      .bindHttp(8080, "127.0.0.1")
      .mountService(service, "/")
      .serve
}