package com.drwal

import akka.actor.{Actor, ActorSystem, IndirectActorProducer, Props}
import akka.event.Logging
import akka.io.IO
import com.drwal.user.{UserReactiveDao, UserDao, UserEndpoint}
import com.drwal.utils.Mongo
import reactivemongo.api.{MongoConnection, MongoDriver}
import spray.can.Http
import scala.concurrent._
import scala.util.{Failure, Success, Try, Properties}
import reactivemongo.api.DB

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
  log.info("Boot actor on-drwal-can")


  def connnectToDb(): Future[DB] = {
    val p = Promise[DB]()
    val driver = new MongoDriver

    val url = Properties.envOrElse("MONGOLAB_URI", "localhost" )
    log.info("MONGOLAB_URI not there = " + url)

    val connection: Try[MongoConnection] =
      MongoConnection.parseURI(url).map { parsedUri =>
        log.info("MONGOLAB_URI parsedUri = " + parsedUri)
        driver.connection(parsedUri)
      }

      connection match {
        case Success(con) => {
          log.info("Connection = " + con.toString)
          import scala.concurrent.ExecutionContext.Implicits.global

          p.success(con("heroku_app33528479"))
        }
        case Failure(e) => throw new IllegalStateException("should not have come here")
      }
    p.future
  }


  val userDao: UserDao = new UserReactiveDao(connnectToDb(), system)
  val service = system.actorOf(Props(classOf[DependencyInjector], userDao), name = "execution")

  IO(Http) ! Http.Bind(service, "0.0.0.0", Properties.envOrElse("PORT", "8080").toInt)
  //val service = system.actorOf(Props(classOf[MyServiceActor], userDao), "demo-service")
  log.info("Backend Service End")
}
