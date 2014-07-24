package pinocchio.api

import pinocchio.core.{CoreActors, ApiCore, SettingsCore, BootedCore}
import akka.io.IO
import spray.can.Http

object WebApp extends BootedCore with SettingsCore with CoreActors with ApiCore with App  {

  IO(Http) ! Http.Bind(rootService, interface = settings.apiUri, port = settings.apiPort)
}
