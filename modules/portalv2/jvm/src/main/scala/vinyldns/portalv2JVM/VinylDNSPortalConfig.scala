package vinyldns.portalv2JVM

import com.typesafe.config.{Config, ConfigFactory}

object VinylDNSPortalConfig {

  lazy val config: Config = ConfigFactory.load()
  lazy val portalConfig: Config = config.getConfig("vinyldns-portal")

  lazy val cryptoConfig: Config = portalConfig.getConfig("crypto")
}
