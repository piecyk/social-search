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
import org.slf4j.LoggerFactory

import com.drwal.utils.BackendConfig.TwitterConfig


object TwitterBearerToken extends TwitterBearerToken {

  val consumerKey = TwitterConfig.consumerKey
  val consumerSecret = TwitterConfig.consumerSecret

  val credentials = Base64.encodeBase64String(s"$consumerKey:$consumerSecret".getBytes())

  def getBearerToken: String = {
    generateBearerToken(credentials)
  }
}

trait TwitterBearerToken {
  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures
  val log = LoggerFactory.getLogger(getClass)


  // setup request/response logging
  val logRequest: HttpRequest => HttpRequest = { r => log.debug(r.toString); r }
  val logResponse: HttpResponse => HttpResponse = { r => log.debug(r.toString); r }


  def generateBearerToken(credentials: String): String = {
    import com.drwal.twitter.TwitterJsonProtocol.twitterTokenFormat
    import SprayJsonSupport._

    val pipeline: HttpRequest => Future[TwitterToken] = (
      addHeader("Authorization", s"Basic $credentials")
        ~> addHeader("Accept", "application/json")
        //~> logRequest
        //~> encode(Gzip)
        ~> sendReceive
        //~> decode(Deflate)
        ~> unmarshal[TwitterToken]
    )

    val formData = Map("grant_type" -> "client_credentials")

    val response = pipeline {
      Post("https://api.twitter.com/oauth2/token", FormData(formData))
    }
    Await.result(response, 10 seconds).access_token
  }
}
