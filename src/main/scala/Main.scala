import java.awt.Desktop
import java.net.{InetSocketAddress, URI}

import _root_.argonaut.Argonaut._
import info.chinnews.instagram._

import org.http4s._
import org.http4s.server._
import org.http4s.dsl._
import org.http4s.server.jetty.JettyBuilder


import scalaz._, Scalaz._
import _root_.argonaut._

import spray.json._
import DefaultJsonProtocol._

import org.mongodb.scala.{MongoDatabase, MongoClient}

import scalaj.http.Http



object Main {

  val client_id : String = "??"
  val client_secret : String = "??"

  implicit def SearchResultCodecJson: CodecJson[SearchResult] =
    casecodec1(SearchResult.apply, SearchResult.unapply)("meta")


  def main(args: Array[String]): Unit = {

    val service = HttpService {
      case req@Method.GET -> Root =>

        val mongoClient: MongoClient = MongoClient()
        val database: MongoDatabase = mongoClient.getDatabase("chin-news")



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
          print(access_token)


//        val searchLocation = Http("https://api.instagram.com/v1/locations/835701820/media/recent")
//          .param("access_token", access_token).asString
//        print(searchLocation)

//          val searchLocation = Http("https://api.instagram.com/v1/locations/search")
//            .param("lat", "48.858844")
//            .param("lng", "2.294351")
//            .param("access_token", access_token).asString
//          print(searchLocation)

        val searchResult = Http("https://api.instagram.com/v1/media/search")
          .param("lat", "48.858844")
          .param("lng", "2.294351")
          .param("access_token", access_token).asString.body.toJson.convertTo[SearchResult]

//        val jsonResult = Parse.parseWith(body, _.field("access_token").flatMap(_.string).get, msg => msg)
        print(searchResult.meta)




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