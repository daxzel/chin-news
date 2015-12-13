package info.chinnews.instagram

import java.io.ByteArrayInputStream
import java.nio.charset.Charset

import akka.actor.Actor
import argonaut.Parse
import com.chinnews.Instagram
import com.chinnews.Instagram.SubscriptionUpdate
import com.google.protobuf.{ExtensionRegistry, MessageLite, Message}
import com.googlecode.protobuf.format.JsonFormat
import com.typesafe.scalalogging.Logger
import org.apache.commons.io.IOUtils
import org.apache.http.{HttpEntityEnclosingRequest, Consts}
import org.apache.http.impl.io.{DefaultHttpRequestParser, SessionInputBufferImpl, HttpTransportMetricsImpl, DefaultHttpResponseParser}
import org.slf4j.LoggerFactory

/**
  * Created by Tsarevskiy
  */
class InstagramMediaActor extends Actor {

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def receive() = {
    case message: String =>
      logger.info(s"Received a request: $message")

      val sessionInputBuffer = new SessionInputBufferImpl(new HttpTransportMetricsImpl(), 2048)
      sessionInputBuffer.bind(new ByteArrayInputStream(message.getBytes(Consts.ASCII)))
      val requestParser = new DefaultHttpRequestParser(sessionInputBuffer)
      requestParser.parse() match {
        case request: HttpEntityEnclosingRequest =>
          if (request.getEntity != null) {
            val content = request.getEntity.getContent

            val builder = Instagram.SubscriptionUpdate.newBuilder()
            val jsonFormat = new JsonFormat
            jsonFormat.merge(content, ExtensionRegistry.getEmptyRegistry, builder)
            val subscriptionUpdate = builder.build()

            logger.info(s"Subscription id: " + subscriptionUpdate.getSubscriptionId)
          }
        case default: _ => logger.info("Unrecognized request " + default.toString)
      }
  }

}
