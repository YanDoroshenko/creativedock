package com.github.yandoroshenko.creativedock

import cats.effect.IO
import fs2.StreamApp
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object RestController extends StreamApp[IO] with Http4sDsl[IO] with Logger {

  private final val Address = "127.0.0.1"
  private final val Port = 443
  private final val RequestTimeoutMs = 1000

  val service: HttpService[IO] = {
    HttpService[IO] {
      case GET -> Root / group / "messages" =>
        log.info("List messages for %s", group)
        Storage.listMessages(group) match {
          case Some(i) if i.nonEmpty =>
            Ok(
              Json.obj(
                "name" -> Json.fromString(group),
                "messages" -> Json.fromValues(i.map(m => Json.fromString(m)))
              )
            )
          case _ =>
            NotFound("NOT FOUND")
        }

      case req @ PUT -> Root / group / "messages" =>
        req.as[Json].map(_ \\ "message").flatMap {
          case l if l.nonEmpty =>
            l.head.asString match {
              case Some(message) => Ok(Producer.send(Messages(), group, message).map(_ => ""))
              case _ => BadRequest("Message must be a string")
            }
          case _ => BadRequest("Request should contain a message")
        }

      case req @ POST -> Root =>
        req.as[Json].map(_ \\ "name").flatMap {
          case l if l.nonEmpty =>
            l.head.asString match {
              case Some(name) => Ok(Producer.send(Groups(), "create", name).map(_ => ""))
              case _ => BadRequest("Group name must be a string")
            }
          case _ => BadRequest("Request should contain a new group's name")
        }

      case DELETE -> Root / group =>
        Ok(Producer.send(Groups(), "delete", group).map(_ => ""))
    }
  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    MessagesConsumer
    GroupConsumer
    BlazeBuilder[IO]
      .bindHttp(Port, Address)
      .mountService(service, "/groups")
      .serve
  }
}
