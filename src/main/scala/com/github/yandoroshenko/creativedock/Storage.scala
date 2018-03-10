package com.github.yandoroshenko.creativedock

import scala.collection.mutable
import scala.util.{Failure, Try}

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 10.03.2018.
  */
object Storage extends Logger {
  private val groups = mutable.Map[String, Seq[String]]()

  def createGroup(name: String): Unit =
    Try {
      groups.synchronized(groups(name) = Stream())
    } match {
      case Failure(e) => log.error(e.getLocalizedMessage(), e.getStackTrace().mkString("\n"))
      case _ => log.info(String.format("Created group %s", name))
    }

  def deleteGroup(name: String): Unit =
    Try {
      groups.synchronized(groups -= name)
    } match {
      case Failure(e) => log.error(e.getLocalizedMessage(), e.getStackTrace().mkString("\n"))
      case _ => log.info(String.format("Deleted group %s", name))
    }

  def putMessage(group: String, message: String): Unit =
    if (!groups.contains(group))
      Failure(new NoSuchElementException(String.format("Group %s does not exist", group)))
    else
      Try(groups.synchronized(groups(group) = groups(group) :+ message)) match {
        case Failure(e) => log.error(e.getLocalizedMessage(), e.getStackTrace().mkString("\n"))
        case _ => log.info(String.format("Put message %s to group %s", message, group))
      }

  def listMessages(group: String): Option[Seq[String]] =
    Option(groups.getOrElse(group, null))
}
