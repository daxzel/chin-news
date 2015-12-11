package info.chinnews.instagram

import akka.actor.Actor

case class InstagramHttpMessage(name: String)

/**
  * Created by Tsarevskiy
  */
class MediaSaveActor extends Actor {

  def receive() = {
    case InstagramHttpMessage(msg) =>
      println("InstagramHttpMessage")

    case _ => println("huh?")
  }

}
