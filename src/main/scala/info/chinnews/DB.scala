package info.chinnews

import org.mongodb.scala._

/**
  * Created by tsarevskiy on 12/11/15.
  */
case class DB(dbname: String, host: String, port: Int) {

  val observer = new Observer[Completed] {
    override def onNext(result: Completed): Unit = {}
    override def onError(e: Throwable): Unit = println("Failed")
    override def onComplete(): Unit = {}
  }

  val mongoClient: MongoClient = MongoClient(s"mongodb://$host:$port")
  val database: MongoDatabase = mongoClient.getDatabase(dbname)

  val userLocations = database.getCollection("user_locations")

  val cities = database.getCollection("cities")


  def storeUserLocation(city_id: String, username: String): Unit = {
    userLocations.insertOne(Document("city_id" -> city_id, "username" -> username)).subscribe(observer)
  }

  def forAllCities(f : (Document) => _) {
    cities.find().foreach( document => f(document))
  }

}
