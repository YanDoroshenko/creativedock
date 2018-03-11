package com.github.yandoroshenko.creativedock.kafka

import com.github.yandoroshenko.creativedock.util.Storage
import com.github.yandoroshenko.creativedock.{Groups, Topic}
import org.apache.kafka.clients.consumer.ConsumerRecord

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 10.03.2018.
  */
object GroupConsumer extends Consumer {
  override protected val topic: Topic = Groups()

  watch

  override protected def act(i: Iterator[ConsumerRecord[String, String]]): Unit =
    i.foreach(r => r.key() match {
      case "create" => Storage.createGroup(r.value())
      case "delete" => Storage.deleteGroup(r.value())
    })
}
