import java.awt.Desktop
import java.net.URI
import java.util.UUID

import _root_.argonaut.Argonaut._
import _root_.argonaut._
import info.chinnews.instagram._
import org.http4s._
import org.http4s.dsl._
import org.http4s.server._
import org.http4s.server.jetty.JettyBuilder
import org.mongodb.scala._

import scala.util.parsing.json.JSONArray
import scalaj.http.Http



object Main {

  val client_id : String = "??"
  val client_secret : String = "??"

  implicit def SearchResultCodecJson: CodecJson[SearchResult] =
    casecodec1(SearchResult.apply, SearchResult.unapply)("meta")



  def main(args: Array[String]): Unit = {

    val service = HttpService {
      case req@Method.GET -> Root =>

        try {

          val mongoClient: MongoClient = MongoClient()
          val database: MongoDatabase = mongoClient.getDatabase("chin-news")


          val userLocations = database.getCollection("user_locations")


          //        MongoDatabase.
          //
          //        val doc: Document = Document();

          val code = req.params.get("code").get
          val body = Http("https://api.instagram.com/oauth/access_token")
            .postForm(Seq("client_id" -> client_id,
              "client_secret" -> client_secret,
              "grant_type" -> "authorization_code",
              "redirect_uri" -> "http://localhost:8080",
              "code" -> code)).asString.body

          val access_token = Parse.parseWith(body, _.field("access_token").flatMap(_.string).get, msg => msg)


          //        val searchLocation = Http("https://api.instagram.com/v1/locations/835701820/media/recent")
          //          .param("access_token", access_token).asString
          //        print(searchLocation)

          //                    val searchLocation = Http("https://api.instagram.com/v1/locations/search")
          //                      .param("lat", "48.858844")
          //                      .param("lng", "2.294351")
          //                      .param("access_token", access_token).asString
          //                    print(searchLocation)

          var intersect: Set[String] = null

          do {
            val searchBody = Http("https://api.instagram.com/v1/media/search")
              .param("lat", "51.105643")
              .param("lng", "17.018681")
              .param("distance", "5000")
              .param("access_token", access_token).asString.body

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
              .param("access_token", access_token).asString.body

            val warsawUsers = Parse.parseOption(searchBody2).get.field("data").get.array.get.map(json => json.field("user").get.field("username").toString).toSet

            warsawUsers.foreach(user =>
              userLocations.insertOne(Document("city_id" -> "warsaw", "username" -> user)).subscribe(observer))

            intersect = wroclawUsers.intersect(warsawUsers)
            println(intersect.size)
            Thread.sleep(10000)
          } while (intersect.isEmpty)

          intersect.foreach(println(_))


          //          (jObjectPL >=>
          //            jsonObjectPL("data")).get(searchBody.parse).get

          //          println( searchBody.asJson.field("data").get)
          //          searchBody.asJson.field("data").get.array.get.map(json => json.field("user").get.field("username"))
          //          (jObjectPL >=> jsonObjectPL("data")).get(searchBody.asJson).get.array.get.map(json => json.field("user").get.field("username"))

          //        val jsonResult = Parse.parseWith(body, _.field("access_token").flatMap(_.string).get, msg => msg)
          //        print(searchResult.meta)

        }

        catch {
          case e: Exception => println("exception caught: " + e);
        }


        Ok("result")
    }

    JettyBuilder.bindHttp(8080)
      .mountService(service, "/")
      .run

    Desktop.getDesktop.browse(new URI(
      "https://api.instagram.com/oauth/authorize/?" +
        s"client_id=$client_id&redirect_uri=http://localhost:8080&response_type=code"))
  }

}