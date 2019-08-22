package com.example

object SampleApp extends App {

  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model.{ HttpRequest, HttpResponse }
  import akka.stream.ActorMaterializer
  import akka.http.scaladsl.model.HttpMethods._
  import scala.concurrent.Future
  import scala.util.Properties
  import akka.http.scaladsl.model.Uri

  implicit val system = ActorSystem("SampleSystem")
  implicit val materializer = ActorMaterializer()

  val port = Properties.envOrElse("PORT", "8080").toInt

  // needed for the future map/flatmap in the end
  implicit val executionContext = system.dispatcher

  val requestHandler: HttpRequest => Future[HttpResponse] = {

    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      Future {
        import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
        HttpResponse(entity = HttpEntity(
          ContentTypes.`text/html(UTF-8)`,
          s"<html><body>Akka-http web app is running on Heroku!</body></html>"))
      }

    case r: HttpRequest =>
      r.discardEntityBytes() // important to drain incoming HTTP Entity stream
      Future(HttpResponse(404, entity = "Unknown resource!"))

  }

  Http().bindAndHandleAsync(requestHandler, "0.0.0.0", port)

}
