package yeryomenkom.db

//all custom codecs should go here
trait CustomSlickMysqlProfile extends slick.jdbc.JdbcProfile with slick.jdbc.MySQLProfile {
  override val columnTypes = new JdbcTypes

  // Customise the types...
  class JdbcTypes extends super.JdbcTypes {
    override val localDateTimeType: LocalDateTimeJdbcType = new LocalDateTimeJdbcType
  }
}

object CustomSlickMysqlProfile extends CustomSlickMysqlProfile
