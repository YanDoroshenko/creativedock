package com.github.yandoroshenko.creativedock

import org.apache.kafka.clients.consumer.ConsumerRecord

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
object ListMessagesConsumer extends Consumer {
  override val topic: Topic = ListMessages()

  watch

  def listMessages(group: String): Iterable[ConsumerRecord[String, String]] =
    getMessages.filter(_.key() == group)
}
