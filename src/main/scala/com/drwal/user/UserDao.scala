package com.drwal.user

import akka.actor.ActorSystem
import akka.event.Logging

import reactivemongo.bson.{BSONObjectID, BSONDocument}
import reactivemongo.api.{QueryOpts, DB}
import reactivemongo.api.collections.default._

import scala.concurrent.Future
import reactivemongo.bson.BSONObjectID

trait UserDao {
  def getAll(): Future[List[User]]
  def getById(id: BSONObjectID): Future[Option[User]]
  def create(login: String, password: String, email: String)
}

class UserReactiveDao(db: DB, userCollection: BSONCollection, system: ActorSystem) extends UserDao {
  // TODO: check how to use 'with Logger'
  val log = Logging(system, getClass)

  implicit val context = system.dispatcher
  import UserFormats._

  def getById(id: BSONObjectID): Future[Option[User]] = {
    log.info("Getting user: %s".format(id))
    userCollection.find(BSONDocument("_id" -> id)).one[User]
  }

  def getAll = {
    log.info("Getting all users")
    //  hmm
    val query = BSONDocument("$query" -> BSONDocument())

    userCollection.find(query)
      .cursor[User]
      .collect[List]()
  }

  def create(login: String, password: String, email: String) = {
    log.info("Create user")
    userCollection.insert(new User(Some(BSONObjectID.generate), login, password, email))
  }

}
