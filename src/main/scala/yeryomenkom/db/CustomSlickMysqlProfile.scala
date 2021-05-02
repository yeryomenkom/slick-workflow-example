package yeryomenkom.db

import io.circe.Json
import io.circe.parser.parse

//all custom codecs should go here
trait CustomSlickMysqlProfile extends slick.jdbc.JdbcProfile with slick.jdbc.MySQLProfile {
  override val columnTypes = new JdbcTypes

  // Customise the types...
  class JdbcTypes extends super.JdbcTypes {
    override val localDateTimeType: LocalDateTimeJdbcType = new LocalDateTimeJdbcType
  }

  import api._

  implicit val JsonColumnType =
    MappedColumnType.base[Json, String](_.noSpaces, parse(_).toTry.get)

}

object CustomSlickMysqlProfile extends CustomSlickMysqlProfile
