package com.drwal.authentication

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class AuthRequest(username: String, password: String)

object AuthRequestJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val authRequestFormat = jsonFormat2(AuthRequest)
}
