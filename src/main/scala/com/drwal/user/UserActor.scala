package com.drwal.user

import akka.actor.Actor
import com.drwal.utils.CORSDirective
import spray.http.StatusCodes._
import spray.routing._
import com.drwal.utils.RouteHelper
import scala.util.{Failure, Success}
import reactivemongo.bson.{BSONObjectID}
import com.drwal.authentication.AuthRequest

trait UserActor extends Actor with UserEndpoint {
  val userDao: UserDao

  def actorRefFactory = context

  def receive = runRoute(drwalUserApi)
}

trait UserEndpoint extends HttpService with RouteHelper {

  val userDao: UserDao

  implicit def executionContext = actorRefFactory.dispatcher

  def drwalUserApi = getPathApi(userRoute)

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
      getPath("users" / "^[A-Za-z0-9_.]+$".r) { (username) =>
        import com.drwal.user.UserJsonProtocol._

        onComplete(userDao.getByUsername(username)) {
          case Success(user) => {
            respondWithStatus(OK) {
              complete(user)
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
      } ~
      path("authenticate") {
        import com.drwal.authentication.AuthRequestJsonSupport._

        post {
          entity(as[AuthRequest]) { authRequest =>
            // TODO: whatt are yoy doing ? turn of emacs
            onComplete(userDao.getByUsername(authRequest.username)) {
              case Success(user) => user match {
                case Some(user) => {
                  println("user = " + user)
                  if (user.password == authRequest.password) complete(200, "Yee") else complete(415, "Incorrect credentials")
                }
                case None => complete(415, "Incorrect credentials")
              }
              case Failure(ex) => complete(415, "Incorrect credentials")
            }
          }
        }
      }
}
