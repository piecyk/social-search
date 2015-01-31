package com.drwal.twitter

import akka.actor.ActorSystem

import org.apache.commons.codec.binary.Base64

import scala.concurrent.{Future, future, ExecutionContext, Await}
import scala.concurrent.duration.DurationInt
import scala.util.{Success, Failure}

import spray.http._
import spray.json.DefaultJsonProtocol
import spray.httpx.encoding.{Gzip, Deflate}
import spray.httpx.SprayJsonSupport
import spray.client.pipelining._


object TwitterBearerToken extends TwitterBearerToken {
  val twitterBaseUrl = "https://api.twitter.com"
  val consumerKey = "Wtgms4Haxr0TmuQnHqNRHtFfW"
  val consumerSecret = "THORDh6AP2WUJlZPzguxygorY8fbFB3RdvoMXEqnEsnAk7UEmE"
  val credentials = Base64.encodeBase64String(s"$consumerKey:$consumerSecret".getBytes())

  def getBearerToken: String = {
    generateBearerToken(credentials, twitterBaseUrl)
  }
}

trait TwitterBearerToken {
  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  def generateBearerToken(credentials: String, baseUrl: String): String = {
    import com.drwal.twitter.TwitterJsonProtocol.twitterTokenFormat
    import com.drwal.twitter.BsonTwitterJsonProtocol.bsonTwitterTokenFormat

    import SprayJsonSupport._

    val pipeline: HttpRequest => Future[TwitterToken] = (
      addHeader("Authorization", s"Basic $credentials")
      ~> encode(Gzip)
      ~> sendReceive
      ~> decode(Deflate)
      ~> unmarshal[TwitterToken]
    )
    val response = pipeline {
      Post(s"$baseUrl/oauth2/token", FormData(Map("grant_type" -> "client_credentials")))
    }
    Await.result(response, 5 seconds).access_token
  }
}
