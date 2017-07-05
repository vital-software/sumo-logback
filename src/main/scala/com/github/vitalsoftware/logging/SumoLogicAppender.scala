package com.github.vitalsoftware.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.{AppenderBase, Layout}
import com.sumologic.log4j.aggregation.SumoBufferFlusher
import com.sumologic.log4j.http.{ProxySettings, SumoHttpSender}
import com.sumologic.log4j.queue.{BufferWithFifoEviction, CostBoundedConcurrentQueue}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Create a Logback compatible Appender for sending data to SumoLogic.com.
  * At present, Sumo Logic only support log4j2 (https://github.com/SumoLogic/sumologic-log4j2-appender).
  *
  * In your logback.xml file:
  * {{{
  *  <appender name="SumoAccess" class="com.github.vitalsoftware.logging.SumoLogicAppender">
  *    <url>[collector URL created on SumoLogic.com]</url>
  *    <encoder>
  *      <pattern>%date{yyyy-MM-dd HH:mm:ss,SSS Z} [%level] from %logger - %message%n%xException</pattern>
  *    </encoder>
  *  </appender>
  * }}}
  *
  * Created by apatzer on 7/5/17.
  */
class SumoLogicAppender extends AppenderBase[ILoggingEvent] {

  val logger: Logger = LoggerFactory.getLogger(this.getClass)

  // Default settings
  protected val DEFAULT_CONNECTION_TIMEOUT = 1000       // Connection timeout (ms)
  protected val DEFAULT_SOCKET_TIMEOUT = 60000          // Socket timeout (ms)
  protected val DEFAULT_RETRY_INTERVAL = 10000L         // If a request fails, how often do we retry.
  protected val DEFAULT_MESSAGES_PER_REQUEST = 100L     // How many messages need to be in the queue before we flush
  protected val DEFAULT_MAX_FLUSH_INTERVAL = 10000L     // Maximum interval between flushes (ms)
  protected val DEFAULT_FLUSHING_ACCURACY = 250L        // How often the flushed thread looks into the message queue (ms)
  protected val DEFAULT_MAX_QUEUE_SIZE_BYTES = 1000000L // Maximum message queue size (bytes)

  // Values set by XML configuration
  protected var layout: Layout[ILoggingEvent] = null
  def setLayout(layout: Layout[ILoggingEvent]) = this.layout = layout
  protected var sourceName: String = "sumo-logback-appender"
  def setSourceName(sourceName: String) = this.sourceName = sourceName
  protected var url: String = null
  def setUrl(url: String) = this.url = url

  // SumoLogic API
  protected lazy val queue = new BufferWithFifoEviction[String](DEFAULT_MAX_QUEUE_SIZE_BYTES,
    new CostBoundedConcurrentQueue.CostAssigner[String]() { override def cost(e: String) = e.length.toLong })

  protected lazy val sender = {
    val s = new SumoHttpSender
    s.setRetryInterval(DEFAULT_RETRY_INTERVAL)
    s.setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT)
    s.setSocketTimeout(DEFAULT_SOCKET_TIMEOUT)
    s.setUrl(url)
    s.setProxySettings(new ProxySettings(null, 443, null, null, null, null))
    s.init()
    s
  }

  protected lazy val flusher = new SumoBufferFlusher(
    DEFAULT_FLUSHING_ACCURACY,
    DEFAULT_MESSAGES_PER_REQUEST,
    DEFAULT_MAX_FLUSH_INTERVAL,
    sourceName,
    sender,
    queue)

  // Logback API
  override def start() = {
    super.start()
    flusher.start()
  }

  override def stop() = {
    super.stop()
    try {
      sender.close()
      flusher.stop()
    } catch {
      case e: Exception =>
        logger.error("Unable to close appender", e)
    }
  }

  override def append(event: ILoggingEvent) = {
    val message = layout.doLayout(event)
    try
      queue.add(message)
    catch {
      case e: Exception =>
        logger.error("Unable to insert log entry into log queue.", e)
    }
  }
}
