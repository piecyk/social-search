package com.drwal.user

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import spray.httpx.SprayJsonSupport._
import reactivemongo.bson._
import DefaultJsonProtocol._
import MediaTypes._
import scala.util._
import com.drwal.CorsTrait

trait UserActor extends Actor with UserEndpoint {
  val userDao : UserDao
  def actorRefFactory = context
  def receive = runRoute(drwalUserApi)
}

trait UserEndpoint extends HttpService with CorsTrait {

  implicit def executionContext = actorRefFactory.dispatcher

  val userDao : UserDao

  object UserJsonProtocol extends DefaultJsonProtocol {

    implicit object BSONObjectIdProtocol extends RootJsonFormat[BSONObjectID] {
      override def write(obj: BSONObjectID): JsValue = JsString(obj.stringify)
      override def read(json: JsValue): BSONObjectID = json match {
        case JsString(id) => BSONObjectID.parse(id) match {
          case Success(validId) => validId
          case _ => deserializationError("Invalid BSON Object Id")
        }
        case _ => deserializationError("BSON Object Id expected")
      }
    }

    implicit val userFormat = jsonFormat4(User.apply)
  }

  var drwalUserApi = cors { respondWithMediaType(`application/json`) { pathPrefix("api" / "v1") {

    path("users") {
      println("test")
      import UserJsonProtocol._

      onComplete(userDao.getAll()) {
        case Success(value) => complete(value)
        case Failure(ex)    => complete(ex.getMessage)
      }
    }

  }}}

}
