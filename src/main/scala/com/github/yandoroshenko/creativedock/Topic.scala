package com.github.yandoroshenko.creativedock

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
sealed trait Topic {
  val name: String
}

case class Info() extends Topic {
  override val name: String = "info"
}

case class Delete() extends Topic {
  override val name: String = "delete"
}

case class New() extends Topic {
  override val name: String = "new"
}

case class Add() extends Topic {
  override val name: String = "add"
}

object Topic {
  implicit def topic2String(topic: Topic): String = topic.name
}