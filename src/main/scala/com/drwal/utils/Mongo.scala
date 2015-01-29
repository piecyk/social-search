package com.drwal.utils

import com.drwal.Boot._
import com.drwal.user.{UserDao, UserReactiveDao}
import com.drwal.utils.BackendConfig.MongoConfig
import reactivemongo.api.MongoDriver

trait Mongo {

  import scala.concurrent.ExecutionContext.Implicits.global

  println("Mongo")

  val driver = new MongoDriver
  val connection = driver.connection(List(MongoConfig.url))
  val db = connection(MongoConfig.database)

  object MongoUserCollection {
    val userCollection = db("user.collection")

    lazy val userDao: UserDao = new UserReactiveDao(db, userCollection, system)
    userDao.remove()
    println("MongoUserCollection")
  }

}
