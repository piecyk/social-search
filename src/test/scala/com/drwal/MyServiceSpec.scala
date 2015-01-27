package com.drwal

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec extends Specification with Specs2RouteTest with MyService with CorsTrait {
  def actorRefFactory = system

  "MyService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("This is the SocialSearch")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }

    val testRoute = path("test") { cors {
      get {
        complete((200, "'CORS drwal"))
      } ~
      post {
        complete((200, "'CORS drwal update"))
      }
    }}

    "check CorsTriat /test routs" in {
      Get("/test") ~> testRoute ~> check {
        status.intValue === 200
        responseAs[String] must contain("'CORS drwal")
      }
      Post("/test") ~> testRoute ~> check {
        status.intValue === 200
        responseAs[String] must contain("'CORS drwal update")
      }
    }
  }
}
