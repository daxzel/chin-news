package info.chinnews

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import info.chinnews.instagram._

import scala.concurrent.duration.Duration


object Main {

  def main(args: Array[String]): Unit = {

    val applicationFile = new File("./application.conf")
    val conf = if (applicationFile.exists()) {
      ConfigFactory.parseFile(applicationFile)
    } else {
      ConfigFactory.load()
    }

    val actorSystem = ActorSystem()
    val scheduler = actorSystem.scheduler
    implicit val executor = actorSystem.dispatcher
    InstragramAuth(conf.getString("chin_news.instagram.client_id"), conf.getString("chin_news.instagram.client_secret"))
      .auth("//", "//", conf, (accessToken, failureListener) => {

        val db = DB(conf.getString("chin_news.db.name"), conf.getString("chin_news.db.host"),
          conf.getInt("chin_news.db.port"))
        scheduler.schedule(
          initialDelay = Duration(5, TimeUnit.SECONDS),
          interval = Duration(60, TimeUnit.SECONDS),
          runnable = new LocationCrawler(accessToken, failureListener, db))

        scheduler.schedule(
          initialDelay = Duration(5, TimeUnit.SECONDS),
          interval = Duration(20, TimeUnit.SECONDS),
          runnable = new TagCrawler(accessToken, failureListener, db))
      }
      )
  }
}

