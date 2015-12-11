package info.chinnews.instagram

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scalaj.http.Http

/**
  * Created by Tsarevskiy
  */
object Subscriber {

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def subscribeByLocation(lat: String, lng: String, client_id: String, client_secret: String,
                          callback_url: String, id: String): Unit = {
    logger.info(s"Subscribing to location: lat - $lat, lng - $lng")
    val result = Http("https://api.instagram.com/v1/subscriptions/")
      .postForm(
        Seq("client_id" -> client_id,
          "client_secret" -> client_secret,
          "lat" -> lat,
          "lng" -> lng,
          "object" -> "geography",
          "aspect" -> "media",
          "radius" -> "5000",
          "callback_url" -> (callback_url + s"$id/"))).asString.body
    logger.info(s"Response: $result")
  }

  def subscribeByTag(tag: String, client_id: String, client_secret: String,
                     callback_url: String, id: String): Unit = {
    logger.info(s"Subscribing to the tag: lat - $tag")
    val result = Http("https://api.instagram.com/v1/subscriptions/")
      .postForm(
        Seq("client_id" -> client_id,
          "client_secret" -> client_secret,
          "object_id" -> tag,
          "object" -> "tag",
          "aspect" -> "media",
          "callback_url" -> (callback_url + s"$id/"))).asString.body

    logger.info(s"Response: $result")
  }

  def removeOldConnections(client_id: String, client_secret: String): Unit = {
    logger.info(s"Removing old connections")
    val result = Http("https://api.instagram.com/v1/subscriptions/")
      .param("client_secret", client_id)
      .param("client_id", client_id)
      .param("object", "all")
      .method("DELETE").asString.body
    logger.info(s"Removed old connections, result: $result")
  }

}
