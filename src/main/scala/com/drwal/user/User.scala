package com.drwal.user

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class User(username: String,
                password: String, // like a virgin :d
                email: String)

object UserJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val userFormat = jsonFormat3(User)
}

object BsonJsonProtocol {

  import reactivemongo.bson.Macros

  implicit val userBsonFormat = Macros.handler[User]
}

