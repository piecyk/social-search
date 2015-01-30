package com.drwal.utils

import reactivemongo.bson.BSONObjectID
import spray.json._

import scala.util.Success

object HelperJsonProtocol {

  implicit object BSONObjectIdProtocol extends RootJsonFormat[BSONObjectID] {
    override def write(obj: BSONObjectID): JsValue = JsString(obj.stringify)

    override def read(json: JsValue): BSONObjectID = json match {
      case JsString(id) => BSONObjectID.parse(id) match {
        case Success(validId) => validId
        case _ => deserializationError("Invalid BSON Object Id")
      }
      case _ => deserializationError("BSON Object Id expected")
    }
  }

}
