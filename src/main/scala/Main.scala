import java.awt.Desktop
import java.net.{InetSocketAddress, URI}

import org.http4s._
import org.http4s.server._
import org.http4s.dsl._
import org.http4s.server.jetty.JettyBuilder

import scalaj.http.Http


object Main {
  def main(args: Array[String]): Unit = {

    val service = HttpService {
      case req@Method.GET -> Root =>
        val code = req.params.get("code").get
        val result = Http("https://api.instagram.com/v1/locations/search")
          .param("lat", "48.858844")
          .param("lng", "2.294351")
          .param("access_token", code).asString
        print(result)
        Ok("result")
    }

    JettyBuilder.bindHttp(8080)
      .mountService(service, "/")
      .run

    Desktop.getDesktop.browse(new URI(
      "https://api.instagram.com/oauth/authorize/?" +
        "client_id=??&redirect_uri=http://localhost:8080&response_type=code"))
  }

}