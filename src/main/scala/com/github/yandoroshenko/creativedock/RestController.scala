
package com.github.yandoroshenko.creativedock

import cats.effect.IO
import fs2.StreamApp
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, _}

object RestController extends StreamApp[IO] with Http4sDsl[IO] with Logger {

  private final val Address = "127.0.0.1"
  private final val Port = 8080
  private final val RequestTimeoutMs = 1000

  val service: HttpService[IO] = {
    HttpService[IO] {
      case GET -> Root / name / "messages" =>
        log.info("List messages for " + name)
        ListMessagesConsumer.listMessages(name) match {
          case i if i.nonEmpty =>
            Ok(i.map(_.value()).mkString("\n"))
          case _ =>
            NotFound("NOT FOUND")
        }
      case PUT -> Root / name / "messages" / message =>
        // TODO Consider running future in the background and just returning 201 CREATED
        Await.result(Producer.send(ListMessages(), name, message).map(_ => Created()), Duration(RequestTimeoutMs, MILLISECONDS))
    }
  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    ListMessagesConsumer
    BlazeBuilder[IO]
      .bindHttp(Port, Address)
      .mountService(service, "/groups")
      .serve
  }
}
