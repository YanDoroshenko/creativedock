package com.github.yandoroshenko.creativedock

import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.KafkaConsumer.Conf
import com.github.yandoroshenko.creativedock.Topic._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
trait Consumer extends Logger {
  protected val topic: Topic

  protected val consumer = KafkaConsumer(Conf(new StringDeserializer(), new StringDeserializer(), groupId = "group"))

  def receive: Iterator[ConsumerRecord[String, String]] = {
    log.info(String.format("Reading topic %s", topic + ""))
    val res = consumer.poll(Long.MaxValue).iterator().asScala
    log.info(res.mkString)
    res
  }

  def getInfo(name: String): Option[Future[Option[ConsumerRecord[String, String]]]] = {
    log.info(String.format("Reading topic %s", topic + ""))
    val res = consumer.poll(Long.MaxValue).iterator().asScala.find(r => r.key() == "name" && r.value() == name)
    res.map(_ => {
      Producer.send(topic, name, "value").map(_ => consumer.poll(Long.MaxValue).iterator().asScala.find(r => r.key() == name))
    })
  }
}