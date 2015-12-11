package info.chinnews.instagram.crawlers

import argonaut.Parse
import com.typesafe.scalalogging.Logger
import info.chinnews.DB
import info.chinnews.instagram.FailureListener
import org.slf4j.LoggerFactory

import scalaj.http.Http

/**
  * Created by tsarevskiy on 12/11/15.
  */
class TagCrawler(accessToken: String, failureListener: FailureListener, db: DB) extends Runnable {
  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def run(): Unit = {
    try {

      logger.info("Query for new media from wroclaw")
      val searchBody = Http("https://api.instagram.com/v1/tags/wroclaw/media/recent")
        .param("access_token", accessToken).asString.body

      val wroclawUsers = Parse.parseOption(searchBody).get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet
      wroclawUsers.foreach(username => db.storeUserLocation("wroclaw", username))

      logger.info("Query for new media from warsaw")
      val searchBody2 = Http("https://api.instagram.com/v1/tags/warsaw/media/recent")
        .param("access_token", accessToken).asString.body

      val warsawUsers = Parse.parseOption(searchBody2).get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet

      warsawUsers.foreach(username => db.storeUserLocation("warsaw", username))

      logger.info("Received users: " + warsawUsers.size)
    } catch {
      case e: Exception =>
        failureListener.notify(e)
        throw e
    }
  }
}
