package info.chinnews.instagram

import akka.actor.Actor

/**
  * Created by Tsarevskiy
  */
class InstagramMediaActor extends Actor {

  def receive() = {
    case message: String => println(s"Actor received a message: $message")
  }

}
