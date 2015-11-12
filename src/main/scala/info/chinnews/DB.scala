package info.chinnews

import org.mongodb.scala._

/**
  * Created by tsarevskiy on 12/11/15.
  */
object DB {

  val observer = new Observer[Completed] {
    override def onNext(result: Completed): Unit = {}
    override def onError(e: Throwable): Unit = println("Failed")
    override def onComplete(): Unit = {}
  }

  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("chin-news")
  val userLocations = database.getCollection("user_locations")

  def storeUserLocation(city_id: String, username: String): Unit = {
    userLocations.insertOne(Document("city_id" -> city_id, "username" -> username)).subscribe(observer)
  }

}
