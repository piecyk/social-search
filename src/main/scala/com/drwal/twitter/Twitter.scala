package com.drwal.twitter

import spray.json.{JsonFormat, DefaultJsonProtocol}
import spray.httpx.SprayJsonSupport


case class TwitterToken(token_type: String, access_token: String)

case class TwitterUser(id: Option[Long], name: Option[String], screen_name: Option[String], profile_image_url: Option[String],
  description: Option[String], lang: Option[String], location: Option[String], favourites_count: Option[Int], followers_count: Option[Int],
  statuses_count: Option[Int], friends_count: Option[Int])

case class Tweet(id: Option[Long], user: Option[TwitterUser], text: Option[String], created_at: Option[String])

object TwitterJsonProtocol extends DefaultJsonProtocol {
  implicit val twitterTokenFormat = jsonFormat2(TwitterToken)
  implicit val twitterUserFormat = jsonFormat11(TwitterUser)
  implicit val tweetFormat = jsonFormat4(Tweet)
}

object BsonTwitterJsonProtocol {
  import reactivemongo.bson.Macros

  implicit val bsonTwitterTokenFormat = Macros.handler[TwitterToken]
  implicit val bsonTwitterUserFormat = Macros.handler[TwitterUser]
  implicit val bsonTweetFormat = Macros.handler[Tweet]
}
