package com.github.yandoroshenko.creativedock

import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.KafkaConsumer.Conf
import com.github.yandoroshenko.creativedock.Topic._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
trait Consumer extends Logger {

  private final val PollTimeoutMs = 200

  protected val topic: Topic

  protected val storage: ListBuffer[ConsumerRecord[String, String]] = ListBuffer[ConsumerRecord[String, String]]()

  protected val consumer = KafkaConsumer(Conf(new StringDeserializer(), new StringDeserializer(), groupId = "group"))

  protected def watch: Unit = {
    log.info(String.format("Subscribing for topic %s", topic + ""))
    consumer.subscribe(List[String](topic).asJava)
    Future {
      while (true)
        storage ++= consumer.poll(PollTimeoutMs).iterator().asScala.toList
    }.onComplete {
      case Failure(e) => log.error(e.getLocalizedMessage(), e.getStackTrace().mkString("\n"))
    }
  }

  def getMessages: ListBuffer[ConsumerRecord[String, String]] = storage
}
