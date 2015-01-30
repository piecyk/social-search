package com.drwal

import akka.actor.{Actor, ActorSystem, IndirectActorProducer, Props}
import akka.event.Logging
import akka.io.IO
import com.drwal.user.{UserDao, UserEndpoint}
import com.drwal.utils.Mongo
import spray.can.Http

import scala.util.Properties

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

object Boot extends App with DrwalActorSystem with Mongo {
  val log = Logging(system, getClass)

  val service = system.actorOf(Props(classOf[DependencyInjector], MongoUserCollection.userDao), name = "execution")

  // create and start our service actor
  //val service = system.actorOf(Props(classOf[MyServiceActor], userDao), "demo-service")

  IO(Http) ! Http.Bind(service, "0.0.0.0", Properties.envOrElse("PORT", "8080").toInt)
  log.info("Backend Service Ready")
}
