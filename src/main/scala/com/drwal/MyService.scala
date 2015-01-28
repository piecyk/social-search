package com.drwal

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import DefaultJsonProtocol._
import MediaTypes._
import spray.httpx.SprayJsonSupport

class MyServiceActor extends Actor with MyService {

  def actorRefFactory = context

  def receive = runRoute(myRoute ~ drwalApp ~ drwalApi)
}

// TODO: move this
trait MyService extends HttpService with CorsTrait {

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

  // TODO: just for fun, remove in prod
  val drwalApp = {
    path("app") {
      compressResponse() {
        getFromResource("app/index.html")
      }
    } ~
      pathPrefix("app") {
        compressResponse() {
          getFromResourceDirectory("app/")
        }
      }
  }

  // TODO: https://gagnechris.wordpress.com/2013/09/15/building-restful-apis-with-scala-using-spray/
  val drwalApiVersion = "v1"

  case class AuthRequest(login: String, password: String)

  object AuthRequestJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
    implicit val PortofolioFormats = jsonFormat2(AuthRequest)
  }

  import AuthRequestJsonSupport._

  var drwalApi = cors {
    pathPrefix("api" / drwalApiVersion) {
      path("test") {
        val source = """{ "some": "JSON source" }"""
        val jsonAst = source.parseJson

        respondWithMediaType(`application/json`) {
          requestContext => requestContext.complete(jsonAst.prettyPrint)
        }
      } ~
      path("authenticate") {
        post {
          entity(as[AuthRequest]) { authRequest =>
            println(authRequest);

            respondWithMediaType(`application/json`) {
              requestContext =>
              if (isValidUser(authRequest)) {
                requestContext.complete("""{"status":"ok"}""")
              } else {
                requestContext.complete(415, "Incorrect credentials")
              }
            }
          }
        }
      }
    }
  }

  /**
   * TODO temporary hard-coded...
   * @return whether user is valid and should be authenticated or not
   */
  def isValidUser(authRequest: AuthRequest) = {
    authRequest.login == "login" && authRequest.password == "pass"
  }
}
