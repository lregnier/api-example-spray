package pinocchio.domain

import spray.http.Uri

trait UserModel {
  val username: String
  val password: String
}

trait Persistence {
  val id: Long
}

trait Representation {
  val href: String
}

case class User(username: String, password: String) extends UserModel
case class UserPersistence(id: Long, username: String, password: String) extends UserModel with Persistence
case class UserRepresentation(href: String, username: String, password: String) extends UserModel with Representation
case class Link(href: String) extends Representation

object UserPersistence {

  def fromUser(user: User, i: Long): UserPersistence = {
    UserPersistence(i, user.username, user.password)
  }
  
  def toUserRepresentation(userPersistence: UserPersistence, uri: Uri): UserRepresentation = {
    UserRepresentation(uri.toString(), userPersistence.username, userPersistence.password)
  }

  def totoUserRepresentationLinkList(userPersistenceList: List[UserPersistence], uri: Uri): List[Link] = {
    val baseUri = uri.toString()
    userPersistenceList.map(x => Link(s"${baseUri}/${x.id}"))
  }
}
