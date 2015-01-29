package com.drwal.user

import akka.actor.ActorSystem
import akka.event.Logging
import reactivemongo.api.DB
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONObjectID, BSONDocument}

import scala.concurrent.Future

trait UserDao {
  def getAll(): Future[List[User]]

  def getById(id: BSONObjectID): Future[Option[User]]

  def create(login: String, password: String, email: String)

  def remove()
}

class UserReactiveDao(db: DB, userCollection: BSONCollection, system: ActorSystem) extends UserDao {
  val log = Logging(system, getClass)

  implicit val context = system.dispatcher

  def getAll: Future[List[User]] = {
    import com.drwal.user.BsonJsonProtocol._

    userCollection.find(BSONDocument.empty).cursor[User].collect[List]()
  }

  def getById(id: BSONObjectID): Future[Option[User]] = {
    import com.drwal.user.BsonJsonProtocol._

    userCollection.find(BSONDocument("id" -> id)).one[User]
  }

  def create(login: String, password: String, email: String) = {
    import com.drwal.user.BsonJsonProtocol._

    userCollection.insert(new User(Some(BSONObjectID.generate), login, password, email))
  }

  def remove = {
    userCollection.remove(BSONDocument.empty)
  }

}
