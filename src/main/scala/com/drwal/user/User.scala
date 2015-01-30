package com.drwal.user

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class User(username: String, password: String, email: String)
case class UserResponce(username: String, email: String)

object UserJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userFormat = jsonFormat3(User)
}

object UserResponceJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val userResponceFormat = jsonFormat2(UserResponce)
}

object BsonJsonProtocol {
  import reactivemongo.bson.Macros

  implicit val userBsonFormat = Macros.handler[User]
  implicit val userResponceBsonFormat = Macros.handler[UserResponce]
}

