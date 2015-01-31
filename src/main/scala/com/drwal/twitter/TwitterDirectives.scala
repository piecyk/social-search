package com.drwal.twitter

import shapeless.{HNil, ::}
import spray.routing.{Directive, Directives}
import Directives.provide


trait TwitterBearerTokenDirective {
  def twitterBearerToken: Directive[String :: HNil] = {
    provide(TwitterBearerToken.getBearerToken)
  }
}

object TwitterBearerTokenDirective extends TwitterBearerTokenDirective
