ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

lazy val versions = new {
  val logback = "1.2.11"
  val finatra = "22.3.0"
  val circe = "0.14.1"
  val scalatest = "3.2.11"
  val cats = "2.10.0"
  val mockito = "4.2.0"
}

lazy val root = (project in file("."))
  .settings(
    name := "weatherfinal",
    libraryDependencies := Seq(
      "com.twitter" %% "finatra-http-client" % versions.finatra,
      "com.twitter" %% "finatra-http-server" % versions.finatra,
      "ch.qos.logback" % "logback-classic" % versions.logback,
      "org.typelevel" %% "cats-core" % versions.cats,
      "io.circe" %% "circe-core" % versions.circe,
      "io.circe" %% "circe-generic" % versions.circe,
      "io.circe" %% "circe-parser" % versions.circe,
      "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",
      "com.twitter" %% "finatra-http-server" % versions.finatra % "test" classifier "tests",
      "org.scalatest" %% "scalatest" % versions.scalatest % "test",
      "org.mockito" % "mockito-core" % versions.mockito % "test"
    )
  )
