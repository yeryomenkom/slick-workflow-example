package yeryomenkom.db

import FluentSourceCodeGenerator._
import slick.codegen.SourceCodeGenerator
import slick.model.Model

object FluentSourceCodeGenerator {
  type TableName  = String
  type ColumnName = String

  implicit class StringExtensions(val str: String) {

    /** Lowercases the first (16 bit) character. (Warning: Not unicode-safe, uses String#apply) */
    final def uncapitalize: String = str(0).toString.toLowerCase + str.tail

    /**
     * Capitalizes the first (16 bit) character of each word separated by one or more '_'. Lower cases all other characters.
     * Removes one '_' from each sequence of one or more subsequent '_' (to avoid collision).
     * (Warning: Not unicode-safe, uses String#apply)
     */
    final def toCamelCase: String =
      str.toLowerCase
        .split("_")
        .map {
          case "" => "_"
          case s  => s
        } // avoid possible collisions caused by multiple '_'
        .map(_.capitalize)
        .mkString("")
  }
}

class FluentSourceCodeGenerator(
    model: Model,
    additionalImports: List[String] = List.empty,
    includeSchemaNameInSlickTable: Boolean = false,
    customizeParsedType: PartialFunction[String, String] = PartialFunction.empty,
    customizeSlickTableName: TableName => String = tn => s"Db${tn.toCamelCase}Table",
    customizeQueryName: TableName => String = tn => s"Db${tn.toCamelCase}Query",
    customizeEntityName: TableName => String = tn => s"Db${tn.toCamelCase}",
    customizeEntityFieldName: PartialFunction[(TableName, ColumnName), String] = PartialFunction.empty,
    customizeEntityFieldType: PartialFunction[(TableName, ColumnName), String] = PartialFunction.empty
) extends SourceCodeGenerator(model) {

  override def code: String =
    additionalImports.mkString("", "\n", "\n") + super.code

  override def entityName: String => String =
    customizeEntityName

  override def tableName: String => String =
    customizeSlickTableName

  override def parseType(tpe: String): String = {
    val parsed = super.parseType(tpe)
    customizeParsedType.lift(parsed).getOrElse(parsed)
  }

  //TODO Is it really the best place for schema customization?
  override def Table = table => {
    val adjustedTable =
      if (includeSchemaNameInSlickTable) table
      else table.copy(name = table.name.copy(schema = None))

    new Table(adjustedTable) {
      override def TableValue: AnyRef with TableValueDef = new TableValue {
        override def rawName: String =
          customizeQueryName(model.name.table)
      }

      override def Column =
        column =>
          new Column(column) {
            override def rawName: String =
              customizeEntityFieldName
                .lift((table.name.table, column.name))
                .getOrElse(super.rawName)
            override def rawType: String =
              customizeEntityFieldType
                .lift((table.name.table, column.name))
                .getOrElse(super.rawType)
        }
    }
  }
}
