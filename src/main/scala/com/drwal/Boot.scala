package com.drwal

import akka.actor.{Actor, ActorSystem, IndirectActorProducer, Props}
import akka.event.Logging
import akka.io.IO
import com.drwal.user.{UserReactiveDao, UserDao, UserEndpoint}
import com.drwal.utils.Mongo
import reactivemongo.api.{MongoConnection, MongoDriver}
import spray.can.Http

import scala.util.{Failure, Success, Try, Properties}

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

  val log = Logging(system, getClass)

  val URI = System.getProperty("MONGOLAB_URI")
  val driver = new MongoDriver
  val connection: Try[MongoConnection] =
    MongoConnection.parseURI(URI).map { parsedUri =>
      driver.connection(parsedUri)
    }

  connection match {
    case Success(con) => {
      log.info("connection" + con.toString)
      import scala.concurrent.ExecutionContext.Implicits.global

      lazy val userDao: UserDao = new UserReactiveDao(con("heroku_app33528479"), system)
      val service = system.actorOf(Props(classOf[DependencyInjector], userDao), name = "execution")

      IO(Http) ! Http.Bind(service, "0.0.0.0", Properties.envOrElse("PORT", "8080").toInt)
      log.info("Backend Service Ready")
    }
    case Failure(e) => throw new IllegalStateException("should not have come here")
  }

  // create and start our service actor
  //val service = system.actorOf(Props(classOf[MyServiceActor], userDao), "demo-service")
  //IO(Http) ! Http.Bind(service, "0.0.0.0", Properties.envOrElse("PORT", "8080").toInt)
  //log.info("Backend Service Ready")
}
