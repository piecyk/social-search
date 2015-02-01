package com.drwal.utils

import com.typesafe.config.{ Config, ConfigFactory }
import scala.util.{ Properties }

object BackendConfig {

  val config: Config = ConfigFactory.load.getConfig("drwal.config")

  val url = "0.0.0.0"
  val port = Properties.envOrElse("PORT", "8080").toInt

  def dbName(s: String) = {
    val Line = """mongodb://(\w*):(\w*)@([\w\.]*):(\w*)/(\w*)""".r
    val Line(dbUser, dbPass, host, port, dbName) = s
    dbName
  }

  object MongoConfig {
    val uri = Properties.envOrElse("MONGOLAB_URI", "localhost")
    val database = dbName(uri)
  }

  object TwitterConfig {
    val _consumerKey = ""
    val _consumerSecret = ""

    val consumerKey = Properties.envOrElse("CONSUMER_KEY", _consumerKey)
    val consumerSecret = Properties.envOrElse("CONSUMER_SECRET", _consumerSecret)
  }

}
