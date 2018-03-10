
package com.github.yandoroshenko.creativedock

import cats.effect.IO
import fs2.StreamApp
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object RestController extends StreamApp[IO] with Http4sDsl[IO] with Logger {

  private final val Address = "127.0.0.1"
  private final val Port = 8080
  private final val RequestTimeoutMs = 1000

  val service: HttpService[IO] = {
    HttpService[IO] {
      case GET -> Root / group / "messages" =>
        log.info("List messages for " + group)
        Storage.listMessages(group) match {
          case Some(i) if i.nonEmpty =>
            Ok(i.mkString("\n"))
          case _ =>
            NotFound("NOT FOUND")
        }
      case PUT -> Root / group / "messages" / message =>
        Ok(Producer.send(Messages(), group, message).map(_ => ""))
      case POST -> Root / group =>
        Ok(Producer.send(Groups(), "create", group).map(_ => ""))
      case DELETE -> Root / group =>
        Ok(Producer.send(Groups(), "delete", group).map(_ => ""))
    }
  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    MessagesConsumer
    TopicConsumer
    BlazeBuilder[IO]
      .bindHttp(Port, Address)
      .mountService(service, "/groups")
      .serve
  }
}
