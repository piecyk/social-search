package com.drwal.user

import akka.actor.Actor
import com.drwal.utils.CORSDirective
import shapeless.HList
import spray.http.StatusCodes._
import spray.routing._

import scala.util.{Failure, Success}

trait UserActor extends Actor with UserEndpoint {
  val userDao: UserDao

  def actorRefFactory = context

  def receive = runRoute(drwalUserApi)
}

trait UserEndpoint extends HttpService with CORSDirective {

  val userDao: UserDao

  implicit def executionContext = actorRefFactory.dispatcher

  def getPath[L <: HList](pm: PathMatcher[L]) = get & path(pm)

  def postPath[L <: HList](pm: PathMatcher[L]) = post & path(pm)

  def drwalUserApi: Route = pathPrefix("api" / "v1") {
    CORS {
      userRoute
    }
  }

  def userRoute: Route =
    getPath("users") {
      import com.drwal.user.UserJsonProtocol._

      onComplete(userDao.getAll) {
        case Success(users) => {
          respondWithStatus(OK) {
            complete(users)
          }
        }
        case Failure(ex) => {
          val errorMsg = ex.getMessage
          complete(InternalServerError, s"userRoute Error: $errorMsg")
        }
      }
    } ~
      postPath("users" / "new") {
        import com.drwal.user.UserJsonProtocol._

        //entity(as[User]) { user =>
        parameters('login, 'password, 'email) { (login, password, email) =>
          userDao.create(login, password, email)
          respondWithStatus(OK) {
            complete(s"The login is '$login' and the email is '$email'")
          }
        }
      }
}
