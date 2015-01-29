package com.drwal

import akka.actor.{Actor, IndirectActorProducer, Props, ActorSystem}
import akka.io.IO
import akka.event.Logging

import spray.can.Http
import com.typesafe.config.ConfigFactory
import util.Properties
import com.drwal.user._

trait DrwalActorSystem {
  implicit val system = ActorSystem("on-drwal-can")
}

class DependencyInjector(_userDao: UserDao) extends IndirectActorProducer {
  override def actorClass = classOf[Actor]
  override def produce = new MasterInjector{
    val userDao = _userDao
  }
}

trait MasterInjector extends Actor with UserEndpoint {
  val userDao: UserDao
  def actorRefFactory = context
  def receive = runRoute(drwalUserApi)
}

object Boot extends App with DrwalActorSystem {
  // TODO: fix logger dependencies, social-search[ERROR] ERROR StatusLogger No log4j2 configuration file found... ? lol
  val log = Logging(system, getClass)

  // TODO: for testing, move this afer
  import reactivemongo.api._
  import scala.concurrent.ExecutionContext.Implicits.global
  import com.drwal.user._

  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))

  val db = connection("drwal_test")
  val userCollection = db("user.collection")
  val userDao: UserDao = new UserReactiveDao(db, userCollection, system)
  userDao.create("test", "test", "test@wp.pl")
  userDao.create("test1", "test1", "test@wp.pl")
  val service = system.actorOf(Props(classOf[DependencyInjector], userDao), name = "UserActor")

  // create and start our service actor
  //val service = system.actorOf(Props(classOf[MyServiceActor], userDao), "demo-service")
  val port = Properties.envOrElse("PORT", "8080").toInt // for Heroku compatibility

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, "0.0.0.0", port)

  log.info("Backend Service Ready")
}
