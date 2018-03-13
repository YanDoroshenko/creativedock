package com.github.yandoroshenko.creativedock.util

import scala.collection.mutable
import scala.util.{Failure, Try}

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 10.03.2018.
  */
object Storage extends Logger {

  val groups: mutable.Map[String, Seq[String]] = mutable.Map[String, Seq[String]]()

  def createGroup(name: String): Try[String] =
    if (groups.contains(name)) {
      Failure(new IllegalArgumentException(String.format("Group %s already exists", name)))
    }
    else {
      Try {
        groups.synchronized(groups(name) = Stream())
        name
      }
    }

  def deleteGroup(name: String): Try[String] =
    if (!groups.contains(name)) {
      Failure(new NoSuchElementException(String.format("Group %s does not exist", name)))
    }
    else {
      Try {
        groups.synchronized(groups -= name)
        name
      }
    }


  def putMessage(group: String, message: String): Try[(String, String)] =
    if (!groups.contains(group)) {
      Failure(new NoSuchElementException(String.format("Group %s does not exist", group)))
    }
    else {
      Try {
        groups.synchronized(groups(group) = groups(group) :+ message)
        group -> message
      }
    }

  def listMessages(group: String): Option[Seq[String]] = groups.get(group)
}
