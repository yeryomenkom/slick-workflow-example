package yeryomenkom

import org.testcontainers.containers.{FixedHostPortGenericContainer, Network}

import scala.jdk.CollectionConverters._

object Containers {

  object Ports {
    val MySql = 3306
  }

  object Aliases {
    val MySql = "mysql"
  }

  object Version {
    val MySql = "8"
  }

  val SharedNetwork: Network = Network.SHARED

  val MySqlDb       = "dao"
  val MySqlUser     = "user"
  val MySqlPassword = "password"

  def mysql[T <: FixedHostPortGenericContainer[T]](
      db: String = MySqlDb,
      user: String = MySqlUser,
      password: String = MySqlPassword
  ): FixedHostPortGenericContainer[T] =
    new FixedHostPortGenericContainer[T](s"mysql:${Version.MySql}")
      .withEnv(
        Map(
          "MYSQL_RANDOM_ROOT_PASSWORD" -> "yes",
          "MYSQL_DATABASE"             -> db,
          "MYSQL_USER"                 -> user,
          "MYSQL_PASSWORD"             -> password
        ).asJava)
      .withExposedPorts(Ports.MySql)
      .withFixedExposedPort(Ports.MySql, Ports.MySql)
      .withNetwork(SharedNetwork)
      .withNetworkAliases(Aliases.MySql)

}
