package com.github.yandoroshenko.creativedock.util

import org.slf4j.LoggerFactory

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 08.03.2018.
  */
trait Logger {
  protected final val log = LoggerFactory.getLogger(getClass())
}
