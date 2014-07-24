package pinocchio.core

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

trait SettingsCore {
  val settings = new Settings(ConfigFactory.load())
}

class Settings(config: Config) {

  val apiUri	    = config.getString("api.uri")
  val apiPort		  = config.getInt("api.port")

}

