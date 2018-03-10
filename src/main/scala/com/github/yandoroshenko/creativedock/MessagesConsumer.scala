package com.github.yandoroshenko.creativedock

import org.apache.kafka.clients.consumer.ConsumerRecord

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
object MessagesConsumer extends Consumer {
  override val topic: Topic = Messages()

  watch

  override protected def act(i: Iterator[ConsumerRecord[String, String]]): Unit =
    i.foreach((r) => Storage.putMessage(r.key(), r.value()))
}
