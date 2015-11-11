package info.chinnews.instagram

import spray.json.{JsNumber, JsString, JsArray, DefaultJsonProtocol}

import scalaz._, Scalaz._
import argonaut._, Argonaut._


/**
  * Created by tsarevskiy on 11/11/15.
  */
case class SearchResult(meta: String)


object SearchResultProtocol extends DefaultJsonProtocol {
  implicit val searchResultFormat = jsonFormat1(SearchResult)
}
