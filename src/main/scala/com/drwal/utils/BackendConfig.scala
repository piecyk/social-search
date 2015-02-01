package com.drwal.utils

import com.typesafe.config.{Config, ConfigFactory}
import scala.util.{Properties}

object BackendConfig {

  val config: Config = ConfigFactory.load.getConfig("drwal.config")

  val url = "0.0.0.0"
  val port = Properties.envOrElse("PORT", "8080").toInt

  object MongoConfig {
    //private val mongoConfig = config.getConfig("mongodb")

    val uri = Properties.envOrElse("MONGOLAB_URI", "localhost")
    val database = "heroku_app33528479"
  }

  object TwitterConfig {
    val _consumerKey = ""
    val _consumerSecret = ""

    val consumerKey = Properties.envOrElse("CONSUMER_KEY", _consumerKey)
    val consumerSecret = Properties.envOrElse("CONSUMER_SECRET", _consumerSecret)
  }

}
