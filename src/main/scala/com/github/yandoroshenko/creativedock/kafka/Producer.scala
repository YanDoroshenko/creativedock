package com.github.yandoroshenko.creativedock.kafka

import cakesolutions.kafka.KafkaProducer
import cakesolutions.kafka.KafkaProducer.Conf
import com.github.yandoroshenko.creativedock.Topic
import com.github.yandoroshenko.creativedock.Topic._
import com.github.yandoroshenko.creativedock.util.{Configuration, Logger}
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}

import scala.concurrent.Future

// Create a org.apache.kafka.clients.producer.KafkaProducer

import org.apache.kafka.common.serialization.StringSerializer

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
object Producer extends Logger with Configuration {

  protected val producer = KafkaProducer(
    Conf(
      new StringSerializer(),
      new StringSerializer(),
      bootstrapServers = BrokerAddress,
    )
  )

  def send(topic: Topic, k: String, v: String): Future[RecordMetadata] = {
    log.info(String.format("Sending (%s -> %s) to %s", k, v, topic + ""))
    producer.send(new ProducerRecord[String, String](topic, k, v))
  }

  def close: Unit = producer.close()
}
