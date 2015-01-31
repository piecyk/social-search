package com.drwal.user

import akka.actor.ActorSystem
import reactivemongo.api.DB
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument}
import scala.concurrent._
import scala.util.{Success, Failure}
import org.slf4j.LoggerFactory

trait UserDao {
  def getAll(): Future[List[UserResponce]]
  def getByUsername(username: String): Future[Option[UserResponce]]
  def isValidUser(userAuthRequest: UserAuthRequest): Future[Boolean]
  def create(username: String, password: String, email: String)
  def remove()
}

class UserReactiveDao(db: DB, system: ActorSystem) extends UserDao {

  val log = LoggerFactory.getLogger(getClass)
  implicit val context = system.dispatcher
  lazy val userCollection = db("user.collection")

  log.info("userCollection = " + userCollection)

  import com.drwal.user.BsonJsonProtocol._

  def getAll: Future[List[UserResponce]] = userCollection.find(BSONDocument.empty).cursor[UserResponce].collect[List]()

  def getByUsername(username: String): Future[Option[UserResponce]] = userCollection.find(BSONDocument("username" -> username)).one[UserResponce]

  // TODO: re-factor LOL
  def isValidUser(userAuthRequest: UserAuthRequest): Future[Boolean] = {
    val promise = Promise[Boolean]()

    userCollection.find(BSONDocument("username" -> userAuthRequest.username)).one[User] onComplete {
      case Success(user) => {
        user match {
          case Some(user) => if (user.password == userAuthRequest.password) promise.success(true) else promise.success(false)
          case None => promise.success(false)
        }
      }
      case Failure(ex) => promise.success(false)
    }

    promise.future
  }

  def create(username: String, password: String, email: String) = {
    // TODO: impl, check username
    userCollection.insert(new User(username, password, email))
  }

  def remove = userCollection.remove(BSONDocument.empty)

}
