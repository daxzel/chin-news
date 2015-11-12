package info.chinnews.instagram

import argonaut.Parse
import info.chinnews.DB._
import info.chinnews.instagram.InstragramAuth.FailureListener

import scalaj.http.Http

/**
  * Created by tsarevskiy on 12/11/15.
  */
class TagCrawler(accessToken: String, failureListener: FailureListener) extends Runnable {
  def run(): Unit = {
    try {
      val searchBody = Http("https://api.instagram.com/v1/tags/wroclaw/media/recent")
        .param("access_token", accessToken).asString.body

      val wroclawUsers = Parse.parseOption(searchBody).get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet
      wroclawUsers.foreach(username => storeUserLocation("wroclaw", username))

      val searchBody2 = Http("https://api.instagram.com/v1/tags/warsaw/media/recent")
        .param("access_token", accessToken).asString.body

      val warsawUsers = Parse.parseOption(searchBody2).get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet

      warsawUsers.foreach(username => storeUserLocation("warsaw", username))

      println("Received tag users: " + warsawUsers.size)
    } catch {
      case e: Exception =>
        failureListener.notify(e)
        throw e
    }
  }
}
