package com.github.yandoroshenko.creativedock

import com.github.yandoroshenko.creativedock.Topic._

import scala.collection.JavaConverters._

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
object InfoConsumer extends Consumer {
  override val topic: Topic = Info()

  consumer.subscribe(List[String](topic).asJava)
}
