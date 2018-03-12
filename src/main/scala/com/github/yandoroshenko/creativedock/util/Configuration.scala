package com.github.yandoroshenko.creativedock.util

import com.typesafe.config.ConfigFactory

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 12.03.2018.
  */
trait Configuration {

  private val config = ConfigFactory.load()

  protected val Address: String = config.getString("rest.address")

  protected val Port: Int = config.getInt("rest.port")

  protected val RequestTimeoutMs: Int = config.getInt("rest.requestTimeoutMs")

  protected val PollTimeoutMs: Int = config.getInt("kafka.pollTimeoutMs")
}
