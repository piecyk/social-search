package com.drwal.user

import akka.actor.ActorSystem
import reactivemongo.api.DB
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument}
import com.drwal.authentication.AuthRequest

import scala.concurrent.Future

trait UserDao {
  def getAll(): Future[List[UserResponce]]

  def getByUsername(username: String): Future[Option[UserResponce]]

  def isValidUser(authRequest: AuthRequest): Future[Boolean]

  def create(username: String, password: String, email: String)

  def remove()
}

class UserReactiveDao(db: DB, userCollection: BSONCollection, system: ActorSystem) extends UserDao {

  implicit val context = system.dispatcher

  def getAll: Future[List[UserResponce]] = {
    import com.drwal.user.BsonJsonProtocol._

    userCollection.find(BSONDocument.empty).cursor[UserResponce].collect[List]()
  }

  def getByUsername(username: String): Future[Option[UserResponce]] = {
    import com.drwal.user.BsonJsonProtocol._

    userCollection.find(BSONDocument("username" -> username)).one[UserResponce]
  }

  def isValidUser(authRequest: AuthRequest): Future[Boolean] = {
    // TODO: impl
    Future { false }
  }

  def create(username: String, password: String, email: String) = {
    import com.drwal.user.BsonJsonProtocol._

    // TODO: check username
    userCollection.insert(new User(username, password, email))
  }

  def remove = {
    userCollection.remove(BSONDocument.empty)
  }

}
