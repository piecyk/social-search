package com.drwal.utils

import spray.http.HttpHeaders._
import spray.http.HttpMethods._
import spray.http.{AllOrigins, HttpMethods, HttpResponse}
import spray.routing._

trait CORSDirective {
  this: HttpService =>

  private val allowOriginHeader = `Access-Control-Allow-Origin`(AllOrigins)
  private val optionsCorsHeaders = List(
    `Access-Control-Allow-Headers`(
      "Origin, X-Requested-With, Content-Type, Accept, Accept-Encoding, Accept-Language, Host, " +
        "Referer, User-Agent"
    ),
    `Access-Control-Max-Age`(60 * 60 * 24 * 20)  // cache pre-flight response for 20 days
  )

  def CORS[T]: Directive0 = mapRequestContext {
    context => context.withRouteResponseHandling {
      case Rejected(reasons)
          if (context.request.method == HttpMethods.OPTIONS && reasons.exists(_.isInstanceOf[MethodRejection])) => {
            val allowedMethods = reasons.collect { case r: MethodRejection => r.supported }

            context.complete(HttpResponse().withHeaders(
              `Access-Control-Allow-Methods`(OPTIONS, allowedMethods :_*) :: allowOriginHeader :: optionsCorsHeaders
            ))
          }
    }.withHttpResponseHeadersMapped { headers => allowOriginHeader :: headers }
  }
}
