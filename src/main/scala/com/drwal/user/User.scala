package com.drwal.user

import reactivemongo.bson.{BSONObjectID, BSONDocumentReader, BSONDocument}

case class User(
  _id: Option[BSONObjectID],
  login: String,
  password: String,
  email: String
)

object UserFormats {
  import reactivemongo.bson.Macros

  implicit val userFormat = Macros.handler[User]
}
