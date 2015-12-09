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
    val searchBody = Http("https://api.instagram.com/v1/subscriptions/")
      .param("client_id", client_id)
      .param("client_secret", client_secret)
      .param("lat", lat)
      .param("lng", lng)
      .param("object", "geography")
      .param("aspect", "media")
      .param("distance", "5000")
      .param("callback_url", callback_url + s"$id/").asString.body
  }

  def subscribeByTag(tag: String, client_id: String, client_secret: String,
                     callback_url: String, id: String): Unit = {
    logger.info(s"Subscribing to the tag: lat - $tag")
    val searchBody = Http("https://api.instagram.com/v1/subscriptions/")
      .param("client_id", client_id)
      .param("client_secret", client_secret)
      .param("tag", tag)
      .param("object", "tag")
      .param("aspect", "media")
      .param("distance", "5000")
      .param("callback_url", callback_url + s"$id/").asString.body
  }

}
