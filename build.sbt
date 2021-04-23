name := "slick-workflow-example"
version := "0.1"
scalaVersion := "2.13.5"

val slick = "3.3.3"
libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.23",
  "com.typesafe.slick" %% "slick-hikaricp" % slick,
  "org.flywaydb" % "flyway-core" % "6.5.4",

  "org.testcontainers" % "testcontainers" % "1.12.1" % Test,
  "com.typesafe.slick" %% "slick-codegen" % slick % Test,
)