package com.github.yandoroshenko.creativedock

import com.github.yandoroshenko.creativedock.util.Storage
import org.scalatest._

import scala.collection.Seq
import scala.util.{Failure, Success}

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 13.03.2018.
  */
class StorageTest extends FlatSpec with Matchers {

  private final val GroupName = "group"

  "Storage " should " add a new group " in {
    Storage.groups.clear()
    Storage.createGroup(GroupName) shouldBe Success(GroupName)
    Storage.groups shouldBe Map(GroupName -> Seq())
  }

  it should " fail on attempt to add an already existent group " in {
    Storage.groups.clear()
    Storage.groups(GroupName) = Seq()
    Storage.createGroup(GroupName) match {
      case Failure(e) =>
        e.getClass().getSimpleName() shouldBe "IllegalArgumentException"
        e.getMessage() shouldBe String.format("Group %s already exists", GroupName)
      case Success(_) =>
        fail()
    }
  }

  it should " delete existing group " in {
    Storage.groups.clear()
    Storage.groups(GroupName) = Seq()
    Storage.deleteGroup(GroupName) shouldBe Success(GroupName)
  }

  it should " fail on non-existent group deletion " in {
    Storage.groups.clear()
    Storage.deleteGroup(GroupName) match {
      case Failure(e) =>
        e.getClass().getSimpleName() shouldBe "NoSuchElementException"
        e.getMessage() shouldBe String.format("Group %s does not exist", GroupName)
      case Success(_) =>
        fail()
    }
  }

  it should " fail on attempt to add a message to non-existent group " in {
    Storage.groups.clear()
    Storage.putMessage(GroupName, "message") match {
      case Failure(e) =>
        e.getClass().getSimpleName() shouldBe "NoSuchElementException"
        e.getMessage() shouldBe String.format("Group %s does not exist", GroupName)
      case Success(_) =>
        fail()
    }
  }

  it should " add a message to an existent group " in {
    val message = "message"
    Storage.groups.clear()
    Storage.groups(GroupName) = Seq()
    Storage.putMessage(GroupName, message) shouldBe Success(GroupName -> message)
    Storage.groups(GroupName) shouldBe Seq(message)
  }

  it should " list messages if group exists " in {
    val message = "message"
    Storage.groups.clear()
    Storage.groups(GroupName) = Seq(message)
    Storage.listMessages(GroupName) shouldBe Some(Seq(message))
  }

  it should " return None for non-existent group messages list " in {
    Storage.groups.clear()
    Storage.listMessages(GroupName) shouldBe None
  }
}
