package com.drwal


import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.event.Logging
import spray.can.Http
import util.Properties

object Boot extends App {
  
  implicit val system = ActorSystem("on-drwal-can")

  val log = Logging(system, getClass)

  // create and start our service actor
  val service = system.actorOf(Props[MyServiceActor], "demo-service")

  val port = Properties.envOrElse("PORT", "8080").toInt // for Heroku compatibility

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, "0.0.0.0", port)

  log.info("Backend Service Ready")
}
