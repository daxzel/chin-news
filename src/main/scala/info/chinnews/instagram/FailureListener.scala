package info.chinnews.instagram

/**
  * Created by tsarevskiy on 13/11/15.
  */
class FailureListener {
  var listeners: List[Exception => Unit] = Nil
  def listen(listener: Exception => Unit): Unit = {
    listeners ::= listener
  }
  def notify(e: Exception) = for (l <- listeners) l(e)
}
