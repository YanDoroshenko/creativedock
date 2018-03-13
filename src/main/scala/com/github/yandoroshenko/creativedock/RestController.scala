package com.github.yandoroshenko.creativedock

import cats.effect.IO
import com.github.yandoroshenko.creativedock.kafka.{GroupConsumer, MessagesConsumer, Producer}
import com.github.yandoroshenko.creativedock.util.{Configuration, Logger, Storage}
import fs2.StreamApp
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.SSLKeyStoreSupport.StoreInfo
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext.Implicits.global

object RestController extends StreamApp[IO] with Http4sDsl[IO] with Logger with Configuration {

  val service: HttpService[IO] = {
    HttpService[IO] {
      case GET -> Root / group / "messages" =>
        log.info(String.format("List messages for %s", group))
        Storage.listMessages(group) match {
          // INFO removed nonEmpty check, "no-event in group" (200 []) is different from
          // non-existant group (404)
          case Some(i) =>
            Ok(
              Json.obj(
                "name" -> Json.fromString(group),
                "messages" -> Json.fromValues(i.map(Json.fromString))
              )
            )
          case _ =>
            NotFound("NOT FOUND")
        }

      case req @ PUT -> Root / group / "messages" =>
        req.as[Json].map(_ \\ "message").flatMap {
          case l if l.nonEmpty =>
            l.head.asString match {
              case Some(message) =>
                log.info(String.format("Add message %s to group %s", message, group))
                // FIXME disliking that you don't wait for ACK from kafka and just report 200 OK
                // at least return 201 (Accepted) if you ignore acknowledgment,
                // but you should acknowledge that event was added to group
                Accepted(Producer.send(Messages(), group, message).map(_ => ""))
              case _ => BadRequest("Message must be a string")
            }
          case _ => BadRequest("Request should contain a message")
        }

      case req @ POST -> Root =>
        req.as[Json].map(_ \\ "name").flatMap {
          case l if l.nonEmpty =>
            l.head.asString match {
              case Some(name) =>
                log.info(String.format("Create group %s", name))
                // FIXME disliking that you don't wait for ACK from kafka and just report 200 OK
                // at least return 201 (Accepted) if you ignore acknowledgment,
                // but you should acknowledge that group was created
                Accepted(Producer.send(Groups(), "create", name).map(_ => "Request accepted"))
              case _ => BadRequest("Group name must be a string")
            }
          case _ => BadRequest("Request should contain a new group's name")
        }

      case DELETE -> Root / group =>
        log.info(String.format("Delete group %s", group))
        // FIXME disliking that you don't wait for ACK from kafka and just report 200 OK
        // at least return 201 (Accepted) if you ignore acknowledgment,
        // but you should acknowledge that group was deleted
        Accepted(Producer.send(Groups(), "delete", group).map(_ => ""))
    }
  }

  def stream(args: List[String], requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    Runtime.getRuntime().addShutdownHook(
      new Thread() {
        override def run(): Unit = {
          log.warn("Shutting down")
          Producer.close
        }
      })
    MessagesConsumer
    GroupConsumer
    BlazeBuilder[IO]
      .bindHttp(Port, Address)
      .mountService(service, "/groups")
      .withSSL(StoreInfo(KeystorePath, KeystorePassword), KeystorePassword)
      .serve
  }
}
