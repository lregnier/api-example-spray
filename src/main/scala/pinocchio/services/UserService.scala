package pinocchio.services

import akka.actor.{ActorRef, Props, Actor}
import akka.util.Timeout

import akka.pattern._
import scala.concurrent.duration._
import spray.http.MediaTypes._
import spray.http._
import spray.routing._
import scala.concurrent.ExecutionContext
import scala.util.Failure
import scala.Some
import spray.http.HttpResponse
import scala.util.Success
import pinocchio.persistence.UserRepositoryActor
import pinocchio.domain.{User, UserPersistence}
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats
import spray.http.HttpHeaders.Location

trait UserService extends HttpService with Directives with Json4sSupport {

  import UserRepositoryActor._
  import UserPersistence._

  implicit def json4sFormats = DefaultFormats
  implicit val timeout = Timeout(5 seconds)
  implicit val executionContext: ExecutionContext = ExecutionContext.global

  val userRepositoryActor: ActorRef

  val ApiPath = "api"
  val UsersPath = "users"

  val route =
    pathPrefix(ApiPath) {
      path(UsersPath) {
        requestUri {
          uri =>
            post {
              entity(as[User]) {
                user =>
                  ctx => {
                    (userRepositoryActor ? AddUser(user)).onComplete {
                      case Success(v) => ctx.complete(HttpResponse(StatusCodes.Created, HttpEntity.Empty, List(Location(s"${uri}/${v}"))))
                      case Failure(e) => ctx.complete(HttpResponse(StatusCodes.InternalServerError, e.getMessage))
                    }
                  }
              }
            } ~
            get {
              respondWithMediaType(`application/json`) {
                ctx => {
                  (userRepositoryActor ? GetUsers).onComplete {
                    case Success(result: List[UserPersistence]) => ctx.complete(totoUserRepresentationLinkList(result, uri))
                    case Failure(e) => ctx.complete(HttpResponse(StatusCodes.InternalServerError, e.getMessage))
                  }
                }
              }
            }
        }
      } ~
      pathPrefix(UsersPath) {
        path(LongNumber) { userId =>
          requestUri { uri =>
            get {
              respondWithMediaType(`application/json`) {
                ctx => {
                  (userRepositoryActor ? GetUser(userId)).onComplete {
                    case Success(result) => {
                      result match {
                        case Some(user: UserPersistence) => ctx.complete(toUserRepresentation(user, uri))
                        case None => ctx.complete(HttpResponse(StatusCodes.NotFound))
                      }
                    }
                    case Failure(e) => ctx.complete(HttpResponse(StatusCodes.InternalServerError, e.getMessage))
                  }
                }
              }
            }
          }
        }
      }
    }

}

class UserServiceActor(val userRepositoryActor: ActorRef) extends UserService with Actor {
  def actorRefFactory = context
  def receive = runRoute(route)
}
object UserServiceActor {
  def props(userRepositoryActor: ActorRef): Props = Props(classOf[UserServiceActor], userRepositoryActor)
}