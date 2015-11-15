package info.chinnews.instagram

import java.awt.Desktop
import java.lang.ProcessBuilder.Redirect
import java.net.URI
import java.nio.file.{Path, _}
import java.nio.file.attribute.BasicFileAttributes

import argonaut.Parse
import com.typesafe.config.Config
import org.http4s.Method
import org.http4s.dsl._
import org.http4s.server.jetty.JettyBuilder
import org.http4s.server.{HttpService, Server}

import scalaj.http.Http

/**
  * Created by tsarevskiy on 12/11/15.
  */
case class InstragramAuth(client_id: String, client_secret: String) {

  case class CopyDirVisitor(fromPath: Path, toPath: Path) extends SimpleFileVisitor[Path] {

    override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
      Files.copy(file, toPath.resolve(fromPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING)
      FileVisitResult.CONTINUE
    }

    override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
      val targetPath = toPath.resolve(fromPath.relativize(dir))
      if (!Files.exists(targetPath)) {
        Files.createDirectory(targetPath)
      }
      FileVisitResult.CONTINUE
    }
  }


  def auth(name: String, password: String, config: Config, authenticated: (String, FailureListener) => Unit): Unit = {

    val failureListener = new FailureListener
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

          failureListener.listen(e => instagramLogin())
          authenticated(access_token.toString, failureListener)
        }
        catch {
          case e: Exception => println("exception caught: " + e);
        }
        Ok("result")
    }

    println("server started")

    server = JettyBuilder.bindHttp(8080)
      .mountService(service, "/")
      .run

    runPhantomjs(
      config.getString("chin_news.os"),
      config.getString("chin_news.crawler.host"),
      config.getString("chin_news.crawler.port"),
      name,
      password)
  }

  def instagramLogin(): Unit = {
    Desktop.getDesktop.browse(new URI(
      "https://api.instagram.com/oauth/authorize/?" +
        s"client_id=$client_id&redirect_uri=http://localhost:8080&response_type=code"))
  }

  def runPhantomjs(os: String, serverHost: String, serverPort: String, name: String, password: String): Unit = {
    val phantomjsPath = Paths.get(getClass.getClassLoader.getResource(s"phantomjs/$os").toURI)
    val tempDirPath = Files.createTempDirectory("phantomjs")
    Files.walkFileTree(phantomjsPath, new CopyDirVisitor(phantomjsPath, tempDirPath))

    val instagramLoginJsResourcePath =
      Paths.get(getClass.getClassLoader.getResource(s"phantomjs/instagram_login.js").toURI)

    val instagramLoginJsPath = Files.copy(instagramLoginJsResourcePath, tempDirPath.resolve("instagram_login.js"))

    val phantomjsExec = tempDirPath.resolve("bin/phantomjs").toString
    Runtime.getRuntime.exec("chmod u+x " + phantomjsExec)

    val pb = new ProcessBuilder(phantomjsExec,
      instagramLoginJsPath.toString, serverHost, serverPort, name, password)
    pb.redirectOutput(Redirect.INHERIT)
    pb.redirectError(Redirect.INHERIT)
    pb.start().waitFor()
  }

}
