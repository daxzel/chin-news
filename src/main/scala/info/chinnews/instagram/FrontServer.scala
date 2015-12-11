package info.chinnews.instagram

import java.nio.{ByteBuffer, CharBuffer}
import java.nio.channels._
import java.nio.charset.Charset
import java.util.concurrent.Executors

import akka.actor.{Props, ActorSystem}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by Tsarevskiy
  */
object FrontServer {
  private val logger: Logger = LoggerFactory.getLogger(getClass)
  var PORT: Int = 8000

  def subscribe(system: ActorSystem) {
    logger.info("Subscribing to the port: " + PORT)

    Executors.newSingleThreadExecutor().execute(new Runnable {
      override def run(): Unit = {
        try {
          val charset = Charset.forName("ISO-8859-1")
          val encoder = charset.newEncoder()
          val decoder = charset.newDecoder()

          val buffer = ByteBuffer.allocate(512)

          val selector = Selector.open()

          val server = ServerSocketChannel.open()
          server.socket().bind(new java.net.InetSocketAddress(PORT))

          server.configureBlocking(false)
          val serverKey = server.register(selector, SelectionKey.OP_ACCEPT)
          while (true) {
            selector.select()
            val keys = selector.selectedKeys()
            val iterator = keys.iterator()
            while (iterator.hasNext) {
              val key = iterator.next()
              logger.info("Received a key: " + key.toString)
              iterator.remove()

              if (key == serverKey) {
                if (key.isAcceptable) {
                  val client: SocketChannel = server.accept()
                  client.configureBlocking(false)
                  val clientKey = client.register(selector, SelectionKey.OP_READ)
                  clientKey.attach(0)
                }
              } else {
                val client = key.channel().asInstanceOf[SocketChannel]
                if (key.isReadable) {
                  val bytesRead = client.read(buffer)
                  if (bytesRead == -1) {
                    key.cancel()
                    client.close()
                  } else {
                    buffer.flip()
                    val request = decoder.decode(buffer).toString
                    logger.info(request)
                    buffer.clear()

                    val index = request.indexOf("hub.challenge")
                    if (index > 0) {
                      val startIndex = index + 14
                      val endIndex = startIndex + 32
                      val hub_challenge = request.substring(startIndex, endIndex)
                      logger.info("Calculated hub_challenge: " + hub_challenge)
                      client.write(encoder.encode(CharBuffer.wrap(hub_challenge)))
                    } else {
                      system.actorOf(Props[InstagramMediaActor], "instagramMediaActor") ! request
                    }
                    key.cancel()
                    client.close()
                  }

                }
              }
            }
          }
        } catch {
          case e: Exception => logger.error(e.toString)
        }
      }
    })

  }
}