package com.drwal

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import DefaultJsonProtocol._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute ~ drwalApi)
}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
              <h1>This is the SocialSearch based back-end server</h1>
              </body>
            </html>
          }
        }
      }
    }

  // TODO: https://gagnechris.wordpress.com/2013/09/15/building-restful-apis-with-scala-using-spray/
  val drwalApiVersion = "/v1"

  var drwalApi =
    pathPrefix("api") {
      path("test") {
        val source = """{ "some": "JSON source" }"""
        val jsonAst = source.parseJson

        respondWithMediaType(`application/json`) {
          requestContext => requestContext.complete(jsonAst.prettyPrint)
        }
      }
    }
}
