package com.github.yandoroshenko.creativedock

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
sealed trait Topic {
  val name: String = getClass().getSimpleName().toLowerCase()
}

case class ListMessages() extends Topic

case class DeleteTopic() extends Topic

case class CreateTopic() extends Topic

case class PutMessage() extends Topic

object Topic {
  implicit def topic2String(topic: Topic): String = topic.name
}
