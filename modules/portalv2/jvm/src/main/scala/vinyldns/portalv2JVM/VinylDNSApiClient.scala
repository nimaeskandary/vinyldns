package vinyldns.portalv2JVM

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import com.amazonaws.auth.{BasicAWSCredentials, SignerFactory}
import vinyldns.core.crypto.CryptoAlgebra
import vinyldns.core.domain.membership.User

import scala.collection.JavaConverters._
import scala.concurrent.Future

case class VinylDNSApiClient() {
  implicit val system = ActorSystem()

  def executeRequest(): Future[HttpResponse] = {
    val user = User("testUser", "testUserAccessKey", "testUserSecretKey")
    val crypto = CryptoAlgebra.load(VinylDNSPortalConfig.cryptoConfig).unsafeRunSync()
    val request = VinylDNSRequest(
      "GET",
      "http://localhost:9000",
      "/zones"
    )
    val signableRequest = new SignableVinylDNSRequest(request)
    val credentials = new BasicAWSCredentials(user.accessKey, crypto.decrypt(user.secretKey))
    val signer = SignerFactory.getSigner("VinylDNS", "us/east")

    signer.sign(signableRequest, credentials)

    val akkaRequest = fromSignableRequest(signableRequest)

    Http().singleRequest(akkaRequest)
  }

  def fromSignableRequest(signableVinylDNSRequest: SignableVinylDNSRequest): HttpRequest = {
    val headers = signableVinylDNSRequest.getHeaders.asScala
      .map(h => RawHeader(h._1, h._2))
      .toList
    HttpRequest(
      HttpMethods.GET,
      signableVinylDNSRequest.getEndpoint + signableVinylDNSRequest.getResourcePath
    ).withHeaders(headers)
  }
}
