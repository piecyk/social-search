package com.drwal.utils

import reactivemongo.api.{MongoConnection, MongoDriver, DB}
import scala.util.{Failure, Success, Try, Properties}
import scala.concurrent._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import org.slf4j.LoggerFactory

trait MongoHelper {

  def connnectToDb(): Future[DB] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val promise = Promise[DB]()
    val driver = new MongoDriver
    val uri = Properties.envOrElse("MONGOLAB_URI", "localhost" )
    val connection: Try[MongoConnection] = MongoConnection.parseURI(uri).map { parsedUri => driver.connection(parsedUri) }
    
    connection match {
      case Success(con) => promise.success(con("heroku_app33528479"))
      case Failure(e) => throw new IllegalStateException("should not have come here")
    }
    promise.future
  }

  def resolveConnectToDb(): DB = {
    val log = LoggerFactory.getLogger(getClass)
    implicit val timeout = Timeout(10 seconds)
    lazy val db = connnectToDb()

    log.info("resolveConnectToDb = " + db)
    Await.result(db, timeout.duration).asInstanceOf[DB]
  }
  
}
