package com.github.yandoroshenko.creativedock.kafka

import java.util.{Collections, Iterator => JIterator}

import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.KafkaConsumer.Conf
import com.github.yandoroshenko.creativedock.Topic
import com.github.yandoroshenko.creativedock.Topic._
import com.github.yandoroshenko.creativedock.util.{Configuration, Logger}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
trait Consumer extends Logger with Configuration {

  protected val topic: Topic

  protected val consumer = KafkaConsumer(Conf(new StringDeserializer(), new StringDeserializer(), groupId = "group"))

  protected def watch: Unit = {
    log.info(String.format("Subscribing for topic %s", topic + ""))
    consumer.subscribe(Collections.singletonList(topic + ""))
    Future {
      while (true) {
        log.debug("Polling topic %s", topic + "")
        act(consumer.poll(PollTimeoutMs).iterator)
      }
    }.onComplete {
      case Failure(e) => log.error(e.getLocalizedMessage(), e.getStackTrace().mkString("\n"))
      case _ => ()
    }
  }

  protected def act(i: JIterator[ConsumerRecord[String, String]])
}
