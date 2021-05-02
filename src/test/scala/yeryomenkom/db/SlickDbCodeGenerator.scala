package yeryomenkom.db

import com.mysql.cj.jdbc.MysqlDataSource
import slick.jdbc.MySQLProfile
import yeryomenkom.Containers

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object SlickDbCodeGenerator extends App {
  val slickDriver = "yeryomenkom.db.CustomSlickMysqlProfile"
  val dbUrl = s"jdbc:mysql://localhost:${Containers.Ports.MySql}/${Containers.MySqlDb}"
  val dbUser = Containers.MySqlUser
  val dbPassword = Containers.MySqlPassword
  val outputFolder = "./src/main/scala"
  val outputPackage = "yeryomenkom.db"
  val outputObjectName = "DbTablesAndModels"
  val outputFileName = "DbTablesAndModels.scala"

  val mysql = Containers.mysql()
  mysql.start()

  val ds = new MysqlDataSource()
  ds.setUrl(dbUrl)
  ds.setUser(dbUser)
  ds.setPassword(dbPassword)

  FlywayMigrations.migrate(ds)

  val db = MySQLProfile.api.Database.forDataSource(ds, None)

  val ignoreTables = Set("flyway_schema_history")

  val generator = db.run {
    MySQLProfile.defaultTables
      .map(_.filterNot(t => ignoreTables.contains(t.name.name)))
      .flatMap(MySQLProfile.createModelBuilder(_, ignoreInvalidDefaults = true).buildModel)
      .map(model => new FluentSourceCodeGenerator(
        model,
        additionalImports = List("import yeryomenkom.db.CustomSlickMysqlProfile._"),
        customizeParsedType = {
          case "java.sql.Date" => "java.time.LocalDate"
          case "java.sql.Timestamp" => "java.time.LocalDateTime"
        },
        customizeEntityFieldType = {
          case ("fcm_token", "metadata") => "io.circe.Json"
        }
      ))
  }

  val result = generator.map { gen =>
    gen.writeToFile(
      profile = slickDriver,
      folder = outputFolder,
      pkg = outputPackage,
      container = outputObjectName,
      fileName = outputFileName
    )
  }

  Await.result(result, Duration.Inf)
  mysql.stop()
}
