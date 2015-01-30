package com.drwal.utils

import com.typesafe.config.{Config, ConfigFactory}

object BackendConfig {

  val config: Config = ConfigFactory.load.getConfig("drwal.config")

  val environment = System.getProperty("TRAVIS") match {
    case null => "dev"
    case "" => "dev"
    case x: String => x
  }

  object MongoConfig {
    private val mongoConfig = config.getConfig("mongodb")

    var url = ""
    var database = ""

    if (environment == "dev") {
      url = mongoConfig.getString("url")
      database = mongoConfig.getString("database")
    } else {
      url = System.getProperty("MONGOLAB_URI")
      database = mongoConfig.getString("heroku_app33528479")
    }

    // TODO: travis config
  }

}
