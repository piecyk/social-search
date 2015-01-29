package com.drwal.user

import reactivemongo.bson.BSONObjectID
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class User(_id: Option[BSONObjectID],
                login: String,
                password: String, // like a virgin :d
                email: String)

object UserJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {

  import com.drwal.utils.HelperJsonProtocol._

  implicit val userFormat = jsonFormat4(User)
}

object BsonJsonProtocol {

  import reactivemongo.bson.Macros

  implicit val userBsonFormat = Macros.handler[User]
}

