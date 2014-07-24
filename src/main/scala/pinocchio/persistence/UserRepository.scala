package pinocchio.persistence

import collection.mutable.Map
import pinocchio.domain.{User, UserPersistence}
import scala.util.Try
import akka.actor.{Props, Actor, Status}

trait UserRepository {

  import UserPersistence._

  val repository: Map[Long, UserPersistence]
  var index: Long

  def add(user: User): Try[Long] = {
    Try {
      val userPersistence = fromUser(user, index + 1)
      repository += (userPersistence.id -> userPersistence)
      index = userPersistence.id
      userPersistence.id
    }
  }

  def get(userId: Long): Try[Option[UserPersistence]] = {
    Try(repository.get(userId))
  }

  def getAll(): Try[List[UserPersistence]] = {
    Try(repository.values.toList)
  }

}

class UserRepositoryActor extends Actor with UserRepository {

  import UserRepositoryActor._

  val repository: Map[Long, UserPersistence] = Map()
  var index: Long = 0

  def receive = {
    case AddUser(user: User) => sender ! add(user).getOrElse(Status.Failure(UserRepositoryException))
    case GetUser(userId: Long) => sender ! get(userId).getOrElse(Status.Failure(UserRepositoryException))
    case GetUsers => sender ! getAll.getOrElse(Status.Failure(UserRepositoryException))
  }

}
object UserRepositoryActor {

  case class AddUser(user: User)
  case class GetUser(userId: Long)
  object GetUsers

  def props: Props = Props(classOf[UserRepositoryActor])

}

object UserRepositoryException extends Exception