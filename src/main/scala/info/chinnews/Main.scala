package info.chinnews

import _root_.argonaut.Argonaut._
import _root_.argonaut._
import info.chinnews.instagram._
import org.mongodb.scala._

import scalaj.http.Http


object Main {

  val client_id: String = "d2cff4a52524420da45e2f9a967332a5"
  val client_secret: String = "cd8e17532a464edaba2c3b996dc3c8a4"

  implicit def SearchResultCodecJson: CodecJson[SearchResult] =
    casecodec1(SearchResult.apply, SearchResult.unapply)("meta")


  def main(args: Array[String]): Unit = {
    InstragramAuth.auth("", "", accessToken => {
      val mongoClient: MongoClient = MongoClient()
      val database: MongoDatabase = mongoClient.getDatabase("chin-news")
      val userLocations = database.getCollection("user_locations")

      var intersect: Set[String] = null

      do {
        val searchBody = Http("https://api.instagram.com/v1/media/search")
          .param("lat", "51.105643")
          .param("lng", "17.018681")
          .param("distance", "5000")
          .param("access_token", accessToken).asString.body

        val observer = new Observer[Completed] {

          override def onNext(result: Completed): Unit = {}

          override def onError(e: Throwable): Unit = println("Failed")

          override def onComplete(): Unit = {}
        }

        val wroclawUsers = Parse.parseOption(searchBody).get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet
        wroclawUsers.foreach(user =>
          userLocations.insertOne(Document("city_id" -> "wroclaw", "username" -> user)).subscribe(observer))

        val searchBody2 = Http("https://api.instagram.com/v1/media/search")
          .param("lat", "52.215361")
          .param("lng", "21.033016")
          .param("distance", "5000")
          .param("access_token", accessToken).asString.body

        val warsawUsers = Parse.parseOption(searchBody2).get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet

        warsawUsers.foreach(user =>
          userLocations.insertOne(Document("city_id" -> "warsaw", "username" -> user)).subscribe(observer))

        intersect = wroclawUsers.intersect(warsawUsers)
        println(intersect.size)
        Thread.sleep(5000)
      } while (intersect.isEmpty)

      intersect.foreach(println(_))
    }

    )
  }
}

