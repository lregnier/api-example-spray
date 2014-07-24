package pinocchio.core

import akka.actor.ActorSystem
import pinocchio.services.UserServiceActor
import pinocchio.persistence.UserRepositoryActor

trait Core {
  implicit def actorSystem: ActorSystem
}

trait BootedCore extends Core {

  implicit lazy val actorSystem = ActorSystem("akka-spray")

  sys.addShutdownHook(actorSystem.shutdown())
}

trait CoreActors {
  self: Core =>

  val userRepositoryActor = actorSystem.actorOf(UserRepositoryActor.props)

}

trait ApiCore {
  self: CoreActors with Core =>

  private implicit val _ = actorSystem.dispatcher

  val rootService = actorSystem.actorOf(UserServiceActor.props(userRepositoryActor), "node-service")
}