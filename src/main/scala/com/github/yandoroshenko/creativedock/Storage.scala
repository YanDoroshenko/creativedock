package com.github.yandoroshenko.creativedock

import scala.collection.mutable
import scala.util.{Failure, Try}

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 10.03.2018.
  */
object Storage extends Logger {
  private val groups = mutable.Map[String, Seq[String]]()

  def createGroup(name: String): Try[Unit] =
    Try {
      groups.synchronized(groups(name) = Stream())
    }

  def deleteGroup(name: String): Try[Unit] =
    Try {
      groups.synchronized(groups -= name)
    }

  def putMessage(group: String, message: String): Try[Unit] =
    if (!groups.contains(group))
      Failure(new NoSuchElementException(String.format("Group %s does not exist", group)))
    else
      Try(groups.synchronized(groups(group) = groups(group) :+ message))

  def listMessages(group: String): Option[Seq[String]] =
    Option(groups.getOrElse(group, null))
}
