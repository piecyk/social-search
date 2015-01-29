package com.drwal

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import DefaultJsonProtocol._
import MediaTypes._
import spray.httpx.SprayJsonSupport
import scala.util._

import com.drwal.user._

class MyServiceActor extends Actor with MyService {

  def actorRefFactory = context

  def receive = runRoute(myRoute ~ drwalApi)
}

// TODO: move this
trait MyService extends HttpService with CorsTrait {
  //the following line was missing

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html><body><h1>This is the SocialSearch based back-end server.</h1></body></html>
          }
        }
      }
    }

  case class AuthRequest(login: String, password: String)
  object AuthRequestJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val authRequestFormats = jsonFormat2(AuthRequest)
  }
  import AuthRequestJsonSupport._

  // TODO: move this
  // TODO: https://gagnechris.wordpress.com/2013/09/15/building-restful-apis-with-scala-using-spray/
  val drwalApiVersion = "v1"
  var drwalApi = cors { respondWithMediaType(`application/json`) { pathPrefix("api" / drwalApiVersion) {

    path("test") {
      val source = """{ "some": "JSON source" }"""
      val jsonAst = source.parseJson

      requestContext => requestContext.complete(jsonAst.prettyPrint)
    } ~
    path("authenticate") {
      post {
        entity(as[AuthRequest]) { authRequest =>
          println(authRequest);

          requestContext =>
          if (isValidUser(authRequest)) {
            requestContext.complete("""{"status":"ok"}""")
          } else {
            requestContext.complete(415, "Incorrect credentials")
          }
        }
      }
    }

  } } }

  /**
   * TODO temporary hard-coded...
   * @return whether user is valid and should be authenticated or not
   */
  def isValidUser(authRequest: AuthRequest) = {
    authRequest.login == "login" && authRequest.password == "pass"
  }
}
