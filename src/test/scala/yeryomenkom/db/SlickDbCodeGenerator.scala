package yeryomenkom.db

import com.mysql.cj.jdbc.MysqlDataSource
import slick.codegen.SourceCodeGenerator
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

  // TODO Replace this abomination with fancy dsl.
  // One feature I am looking for is the ability of fine-grained field types customizations
  // e.g. I wanna have an ability to provide a function:
  // (Schema, Table, ColumnName) => ScalaFieldType
  val generator = db.run {
    MySQLProfile.defaultTables
      .map(_.filterNot(t => ignoreTables.contains(t.name.name)))
      .flatMap(MySQLProfile.createModelBuilder(_, ignoreInvalidDefaults = true).buildModel)
      .map { model =>
        new SourceCodeGenerator(model) {
          // customize Scala entity name (case class, etc.)
          override def entityName: String => String =
            dbTableName => "Db" + dbTableName.toCamelCase

          // customize Scala table name (table class, table values, ...)
          override def tableName: String => String =
            dbTableName => "Db" + dbTableName.toCamelCase + "Table"

          //TODO How to get table and column name inside of this function???
          override def parseType(tpe: String): String = super.parseType(tpe) match {
            case "java.sql.Date" => "java.time.LocalDate"
            case "java.sql.Timestamp" => "java.time.LocalDateTime"
            case parsed => parsed
          }

          // override generator responsible for tables
          //TODO Is it really the best place for schema customization?
          override def Table = table => new Table(table.copy(name = table.name.copy(schema = None))) {
            // customize table value (TableQuery) name (uses tableName as a basis)
            override def TableValue: AnyRef with TableValueDef = new TableValue {
              override def rawName = super.rawName + "Query"
            }
          }
        }
      }
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
