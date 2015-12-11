package info.chinnews.instagram

import java.io.ByteArrayInputStream

import akka.actor.Actor
import argonaut.Parse
import com.typesafe.scalalogging.Logger
import org.apache.commons.io.IOUtils
import org.apache.http.Consts
import org.apache.http.impl.io.{SessionInputBufferImpl, HttpTransportMetricsImpl, DefaultHttpResponseParser}
import org.slf4j.LoggerFactory

/**
  * Created by Tsarevskiy
  */
class InstagramMediaActor extends Actor {

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def receive() = {
    case message: String =>
      val sessionInputBuffer = new SessionInputBufferImpl(new HttpTransportMetricsImpl(), 2048)
      sessionInputBuffer.bind(new ByteArrayInputStream(message.getBytes(Consts.ASCII)))

      val responseParser = new DefaultHttpResponseParser(sessionInputBuffer)
      val response = responseParser.parse().getEntity.getContent

      logger.info(s"Received a response: $response")

      val warsawUsers = Parse.parseOption(IOUtils.toString(response)).get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet

      warsawUsers.foreach(user => logger.info(s"User :" + user))
  }

}
