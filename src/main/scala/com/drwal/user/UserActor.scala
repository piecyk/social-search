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
      import com.drwal.user.UserResponceJsonProtocol._

      onComplete(userDao.getAll) {
        case Success(users) => {
          complete(users)
        }
        case Failure(ex) => {
          val errorMsg = ex.getMessage
          complete(InternalServerError, s"userRoute Error: $errorMsg")
        }
      }
    } ~
      getPath("users" / "^[A-Za-z0-9_.]+$".r) { (username) =>
        import com.drwal.user.UserResponceJsonProtocol._

        onComplete(userDao.getByUsername(username)) {
          case Success(user) => {
            complete(user)
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
          complete(s"The login is '$login' and the email is '$email'")
        }
      } ~
      path("authenticate") {
        import com.drwal.authentication.AuthRequestJsonSupport._

        post {
          entity(as[AuthRequest]) { authRequest =>
            // TODO: whatt are yoy doing ? turn of emacs
            onComplete(userDao.isValidUser(authRequest)) {
              case Success(valid) => complete(200, "Yee")
              case Failure(ex) => complete(415, "Incorrect credentials")
            }
          }
        }
      }
}
