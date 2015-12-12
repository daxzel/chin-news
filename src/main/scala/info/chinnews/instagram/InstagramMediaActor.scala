package info.chinnews.instagram

import java.io.ByteArrayInputStream

import akka.actor.Actor
import argonaut.Parse
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
          if ( request.getEntity != null ) {
            val content = request.getEntity.getContent
            val warsawUsers = Parse.parseOption(IOUtils.toString(content))
              .get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet
            warsawUsers.foreach(user => logger.info(s"User :" + user))
          }
      }
  }

}
