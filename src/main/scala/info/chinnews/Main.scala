package info.chinnews

import java.util.concurrent.TimeUnit

import _root_.argonaut.Argonaut._
import _root_.argonaut._
import akka.actor.ActorSystem
import info.chinnews.instagram._

import scala.concurrent.duration.Duration


object Main {

  val client_id: String = "d2cff4a52524420da45e2f9a967332a5"
  val client_secret: String = "cd8e17532a464edaba2c3b996dc3c8a4"

  implicit def SearchResultCodecJson: CodecJson[SearchResult] =
    casecodec1(SearchResult.apply, SearchResult.unapply)("meta")


  def main(args: Array[String]): Unit = {

    val actorSystem = ActorSystem()
    val scheduler = actorSystem.scheduler
    implicit val executor = actorSystem.dispatcher
    InstragramAuth.auth("", "", (accessToken, failureListener) => {

      scheduler.schedule(
        initialDelay = Duration(5, TimeUnit.SECONDS),
        interval = Duration(60, TimeUnit.SECONDS),
        runnable = new LocationCrawler(accessToken, failureListener))

      scheduler.schedule(
        initialDelay = Duration(5, TimeUnit.SECONDS),
        interval = Duration(20, TimeUnit.SECONDS),
        runnable = new TagCrawler(accessToken, failureListener))
    }
    )
  }
}

