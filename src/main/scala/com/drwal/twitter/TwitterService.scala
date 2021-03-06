package com.drwal.twitter

import akka.actor.ActorSystem

import scala.concurrent.{Future, future, ExecutionContext, Await}
import scala.concurrent.duration.DurationInt
import scala.util.Try

import spray.http._
import spray.json.DefaultJsonProtocol
import spray.httpx.encoding.{Gzip, Deflate}
import spray.httpx.SprayJsonSupport
import spray.httpx.unmarshalling.{MalformedContent, Unmarshaller, Deserialized}
import spray.client.pipelining._
import org.slf4j.LoggerFactory


object TwitterService extends TwitterService

trait TwitterService {
  val log = LoggerFactory.getLogger(getClass)
  implicit val system = ActorSystem()
  import system.dispatcher // execution context for futures

  def sendAndReceive = sendReceive

  def tweets(bearerToken: String, userName: String):  Future[List[Tweet]] = {
    log.info("bearerToken = " + bearerToken)

    import com.drwal.twitter.TwitterJsonProtocol._
    import SprayJsonSupport._

    // Improve to deserialize error?  https://groups.google.com/forum/#!topic/spray-user/N6RGjXLGC-Q/discussion
    val pipeline: HttpRequest => Future[List[Tweet]] = (
      addHeader("Authorization", s"Bearer $bearerToken")
        ~> encode(Gzip)
        ~> sendAndReceive
        ~> decode(Deflate)
        ~> unmarshal[List[Tweet]]
    )

    pipeline {
      Get(s"https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name=$userName&count=5&include_rts=true&exclude_replies=true")
    }
  }
}
