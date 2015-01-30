package com.drwal.user

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class User(username: String,
  password: String, // like a virgin :d
  email: String)

case class UserResponce(username: String, email: String)

object UserJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userFormat = jsonFormat3(User)
}

object UserResponceJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val responceUserFormat = jsonFormat2(UserResponce)
}

object BsonJsonProtocol {
  import reactivemongo.bson.Macros

  implicit val userBsonFormat = Macros.handler[User]
  implicit val responceUserBsonFormat = Macros.handler[UserResponce]
}

