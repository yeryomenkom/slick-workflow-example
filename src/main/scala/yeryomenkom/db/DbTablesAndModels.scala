package yeryomenkom.db
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object DbTablesAndModels extends {
  val profile = yeryomenkom.db.CustomSlickMysqlProfile
} with DbTablesAndModels

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait DbTablesAndModels {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import yeryomenkom.db.CustomSlickMysqlProfile._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = DbFcmTokenQuery.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table DbFcmTokenQuery
   *  @param token Database column token SqlType(VARCHAR), PrimaryKey, Length(255,true)
   *  @param userId Database column user_id SqlType(INT)
   *  @param deviceType Database column device_type SqlType(VARCHAR), Length(8,true)
   *  @param metadata Database column metadata SqlType(JSON), Length(1073741824,true), Default(None)
   *  @param timeUpdate Database column time_update SqlType(TIMESTAMP) */
  case class DbFcmToken(token: String, userId: Int, deviceType: String, metadata: Option[io.circe.Json] = None, timeUpdate: java.time.LocalDateTime)
  /** GetResult implicit for fetching DbFcmToken objects using plain SQL queries */
  implicit def GetResultDbFcmToken(implicit e0: GR[String], e1: GR[Int], e2: GR[Option[io.circe.Json]], e3: GR[java.time.LocalDateTime]): GR[DbFcmToken] = GR{
    prs => import prs._
    DbFcmToken.tupled((<<[String], <<[Int], <<[String], <<?[io.circe.Json], <<[java.time.LocalDateTime]))
  }
  /** Table description of table fcm_token. Objects of this class serve as prototypes for rows in queries. */
  class DbFcmTokenTable(_tableTag: Tag) extends profile.api.Table[DbFcmToken](_tableTag, "fcm_token") {
    def * = (token, userId, deviceType, metadata, timeUpdate) <> (DbFcmToken.tupled, DbFcmToken.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(token), Rep.Some(userId), Rep.Some(deviceType), metadata, Rep.Some(timeUpdate))).shaped.<>({r=>import r._; _1.map(_=> DbFcmToken.tupled((_1.get, _2.get, _3.get, _4, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column token SqlType(VARCHAR), PrimaryKey, Length(255,true) */
    val token: Rep[String] = column[String]("token", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column user_id SqlType(INT) */
    val userId: Rep[Int] = column[Int]("user_id")
    /** Database column device_type SqlType(VARCHAR), Length(8,true) */
    val deviceType: Rep[String] = column[String]("device_type", O.Length(8,varying=true))
    /** Database column metadata SqlType(JSON), Length(1073741824,true), Default(None) */
    val metadata: Rep[Option[io.circe.Json]] = column[Option[io.circe.Json]]("metadata", O.Length(1073741824,varying=true), O.Default(None))
    /** Database column time_update SqlType(TIMESTAMP) */
    val timeUpdate: Rep[java.time.LocalDateTime] = column[java.time.LocalDateTime]("time_update")
  }
  /** Collection-like TableQuery object for table DbFcmTokenQuery */
  lazy val DbFcmTokenQuery = new TableQuery(tag => new DbFcmTokenTable(tag))
}
