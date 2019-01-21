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

    val port = Properties.envOrElse("PORT", "8080").toInt
    val route = {
      get {
        pathSingleSlash {
          complete {
            HttpEntity(
              ContentTypes.`text/html(UTF-8)`,
              Page.skeleton.render
            )
          }
        } ~
          getFromResourceDirectory("")
      }
    }
    Http().bindAndHandle(route, "0.0.0.0", port = port)
  }
}
