package yeryomenkom.db

import org.flywaydb.core.Flyway

import javax.sql.DataSource

object FlywayMigrations {

  def migrate(dataSource: DataSource): Unit = {
    Flyway
      .configure()
      .locations("migrations/main")
      .dataSource(dataSource)
      .load
      .migrate()
  }

}
