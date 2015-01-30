package com.drwal.user

import akka.actor.Actor
import spray.http.StatusCodes._
import spray.routing._
import com.drwal.utils.RouteHelper
import scala.util.{Failure, Success}
import reactivemongo.bson.{BSONObjectID}

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

        entity(as[User]) { user =>
          userDao.create(user.username, user.password, user.email)
          complete(s"The login is '$user.username' and the email is '$user.email'")
        }
      } ~
      path("authenticate") {
        import com.drwal.user.UserAuthRequestJsonSupport._

        post {
          entity(as[UserAuthRequest]) { userAuthRequest =>
            onComplete(userDao.isValidUser(userAuthRequest)) {
              case Success(valid) => if (valid) complete(OK, "Yee") else complete(415, "Incorrect credentials")
              case Failure(ex) => complete(415, "Incorrect credentials")
            }
          }
        }
      }
}
