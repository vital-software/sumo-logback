package com.github.vitalsoftware.logging

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.LayoutBase
import org.specs2.mutable.Specification

/**
  * Created by apatzer on 7/6/17.
  */
class SumoLogicAppenderTest extends Specification {

  "SumoLogicAppender" should {

    "Attempt connection" in {
      val appender = new SumoLogicAppender
      appender.setUrl("www.google.com")
      appender.setEncoder(new PatternLayoutEncoder)
      appender.start() must not(throwA[Exception])
      appender.stop() must not(throwA[Exception])
    }
  }
}
