package info.chinnews.instagram

import info.chinnews.system.DB
;

/**
  * Created by Tsarevskiy
  */
object CitiesHolder {

  def addCities(db: DB): Unit = {
    db.addCity("warsaw", "52.215361", "17.018681")
    db.addCity("wroclaw", "51.105643", "17.018681")
    db.addCity("moscow", "55.753567", "37.621077")
  }
}
