package vinyldns.portalv2JVM

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.util.Properties

object Server {
  def main(args: Array[String]): Unit = {
    implicit val system: akka.actor.ActorSystem = ActorSystem()
    implicit val materializer: akka.stream.ActorMaterializer = ActorMaterializer()

    val vinylDNSApiClient = VinylDNSApiClient()

    val port = Properties.envOrElse("PORT", "8080").toInt
    val route = {
      (get & path("server")) {
        complete {
          vinylDNSApiClient.executeRequest()
        }
      } ~
        (get & path("portalv2-fastopt-bundle.js")) {
          getFromResource("portalv2-fastopt-bundle.js")
        } ~
        path(""".*""".r) { _ =>
          get {
            complete {
              HttpEntity(
                ContentTypes.`text/html(UTF-8)`,
                Page.skeleton.render
              )
            }
          }
        }
    }

    Http().bindAndHandle(route, "0.0.0.0", port = port)
  }
}
