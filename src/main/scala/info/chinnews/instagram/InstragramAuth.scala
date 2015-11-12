package info.chinnews.instagram

import java.awt.Desktop
import java.net.URI

import argonaut.Parse
import org.http4s.Method
import org.http4s.dsl._
import org.http4s.server.{Server, HttpService}
import org.http4s.server.jetty.JettyBuilder

import scalaj.http.Http

/**
  * Created by tsarevskiy on 12/11/15.
  */
object InstragramAuth {

  val client_id: String = "//"
  val client_secret: String = "//"

  def auth(name: String, password: String, authenticated: String => Unit): Unit = {

    var server: Server = null
    val service = HttpService {
      case req@Method.GET -> Root =>
        try {
          val code = req.params.get("code").get
          val body = Http("https://api.instagram.com/oauth/access_token").postForm(Seq(
            "client_id" -> client_id,
            "client_secret" -> client_secret,
            "grant_type" -> "authorization_code",
            "redirect_uri" -> "http://localhost:8080",
            "code" -> code))
            .asString.body

          val access_token = Parse.parseWith(body, _.field("access_token").flatMap(_.string).get, msg => msg)

          authenticated(access_token.toString)
        }
        catch {
          case e: Exception => println("exception caught: " + e);
        }
        Thread.sleep(20000)
        instagramLogin()
        Ok("result")
    }

    println("server started")

    server = JettyBuilder.bindHttp(8080)
      .mountService(service, "/")
      .run

    instagramLogin()
  }

  def instagramLogin(): Unit = {
    Desktop.getDesktop.browse(new URI(
      "https://api.instagram.com/oauth/authorize/?" +
        s"client_id=$client_id&redirect_uri=http://localhost:8080&response_type=code"))
  }


}
