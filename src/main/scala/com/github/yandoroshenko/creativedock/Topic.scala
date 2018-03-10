package com.github.yandoroshenko.creativedock

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
sealed trait Topic {
  val name: String = getClass().getSimpleName().toLowerCase()
}

case class Messages() extends Topic

case class Groups() extends Topic

object Topic {
  implicit def topic2String(topic: Topic): String = topic.name
}
