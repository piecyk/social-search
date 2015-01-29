package com.drwal.utils

import com.typesafe.config.{Config, ConfigFactory}

object BackendConfig {

  val config: Config = ConfigFactory.load.getConfig("drwal.config")

  lazy val environment = System.getProperty("TRAVIS") match {
    case null => "dev"
    case "" => "dev"
    case x: String => x
  }
  println(environment)

  object MongoConfig {
    private val mongoConfig = config.getConfig("mongodb")

    lazy val url = mongoConfig.getString("url")
    lazy val host = mongoConfig.getString("host")
    lazy val port = mongoConfig.getInt("port")
    lazy val database = mongoConfig.getString("database")

    // TODO: travis config
  }

}
