package com.drwal.utils

import shapeless.HList
import spray.routing._
import spray.http.MediaTypes._

trait RouteHelper extends HttpService with CORSDirective {

  def getPath[L <: HList](pm: PathMatcher[L]) = get & path(pm)

  def postPath[L <: HList](pm: PathMatcher[L]) = post & path(pm)

  def getPathApi(routes: Route): Route = {
    pathPrefix("api" / "v1") {
      respondWithMediaType(`application/json`) {
        CORS {
          routes
        }
      }
    }
  }

}
