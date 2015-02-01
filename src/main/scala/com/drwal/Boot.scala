package com.drwal

import akka.actor.{Actor, ActorSystem, IndirectActorProducer, Props}
import org.slf4j.LoggerFactory
import akka.io.IO
import com.drwal.user.{UserReactiveDao, UserDao, UserEndpoint}
import com.drwal.utils.MongoHelper
import spray.can.Http
import scala.util.{Properties}
import com.drwal.utils.BackendConfig


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


object Boot extends App with DrwalActorSystem with MongoHelper {
  val log = LoggerFactory.getLogger(getClass)

  log.info("Boot actor on-drwal-can")

  // setup the mongoreactive connection
  implicit lazy val db = resolveConnectToDb()

  val userDao: UserDao = new UserReactiveDao(db, system)
  val service = system.actorOf(Props(classOf[DependencyInjector], userDao), name = "execution")

  IO(Http) ! Http.Bind(service, BackendConfig.url, BackendConfig.port)
  log.info("Backend Service End")
  java.lang.Thread.sleep(10000);
}
