package com.drwal.user

import akka.actor.ActorSystem
import reactivemongo.api.DB
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument}

import scala.concurrent._
import scala.util.{Success, Failure}

trait UserDao {
  def getAll(): Future[List[UserResponce]]

  def getByUsername(username: String): Future[Option[UserResponce]]

  def isValidUser(userAuthRequest: UserAuthRequest): Future[Boolean]

  def create(username: String, password: String, email: String)

  def remove()
}

class UserReactiveDao(db: DB, system: ActorSystem) extends UserDao {

  implicit val context = system.dispatcher

  import com.drwal.user.BsonJsonProtocol._

  val userCollection = db("user.collection")

  def getAll: Future[List[UserResponce]] = userCollection.find(BSONDocument.empty).cursor[UserResponce].collect[List]()

  def getByUsername(username: String): Future[Option[UserResponce]] = userCollection.find(BSONDocument("username" -> username)).one[UserResponce]

  // TODO: re-factor LOL
  def isValidUser(userAuthRequest: UserAuthRequest): Future[Boolean] = {
    val p = Promise[Boolean]()
    // p.failure(throw new Exception("Wrong password")) is bad idea? how reject p in scala?
    userCollection.find(BSONDocument("username" -> userAuthRequest.username)).one[User] onComplete {
      case Success(user) => {
        user match {
          case Some(user) => if (user.password == userAuthRequest.password) p.success(true) else p.success(false)
          case None => p.success(false)
        }
      }
      case Failure(ex) => p.success(false)
    }

    p.future
  }

  def create(username: String, password: String, email: String) = {
    // TODO: impl, check username
    userCollection.insert(new User(username, password, email))
  }

  def remove = userCollection.remove(BSONDocument.empty)

}
