
name := "PPS-AmongSus-Konchenkov-Pasqualini-Poggi"

version in ThisBuild := "0.1.3"

scalaVersion in ThisBuild := "2.12.8"
organization in ThisBuild := "it.amongsus"

/*
 * START LIBRARY DEFINITIONS.
 */

// TESTS.
val scalamock = "org.scalamock" %% "scalamock" % "4.4.0" % Test
val scalatest = "org.scalatest" %% "scalatest" % "3.1.0" % Test
val scalacheck = "org.scalacheck" %% "scalacheck" % "1.14.0" % Test

// AKKA ACTORS.
val akkaV = "2.5.13"
val akkaTyped = "com.typesafe.akka" %% "akka-actor-typed" % akkaV
val akkaTest = "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaV
val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaV

// PROLOG
val tuProlog = "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0"

//CATS
val catsCore = "org.typelevel" %% "cats-core" % "2.0.0"
val catsEffect = "org.typelevel" %% "cats-effect" % "2.2.0"

lazy val akkaDependencies = Seq(
  akkaTyped,
  akkaRemote,
  akkaTest % "test"
)

lazy val testDependencies = Seq(
  scalatest,
  scalamock,
  scalacheck
)

lazy val catsDependencies = Seq(
  catsCore,
  catsEffect
)
/*
 * END LIBRARY DEFINITIONS.
 */

/*
 * START PROJECT SETTINGS.
 */
lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "utf8"
)

lazy val commonSettings = Defaults.coreDefaultSettings ++ Seq(
  scalaVersion := "2.12.8",
  version := "0.1",
  crossPaths := false,
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val settings = commonSettings

/*
 * END PROJECT SETTINGS.
 */

/*
 * START PROJECT DEFINITIONS.
 */

// MODEL PROJECT.
lazy val core = Project(
  id = "core",
  base = file("core"))
  .settings(commonSettings)
  .settings(
    name := "core",
    libraryDependencies ++= (testDependencies)
  )

lazy val commons = Project(
  id = "common",
  base = file("commons"))
  .settings(commonSettings)
  .settings(
    name := "commons",
    libraryDependencies ++= (testDependencies :+ akkaTyped)
  ).dependsOn(core)

lazy val server = Project(
  id = "server",
  base = file("server"))
  .enablePlugins(PackPlugin)
  .settings(commonSettings)
  .settings(name := "server",
    libraryDependencies ++= (akkaDependencies ++ testDependencies))
  .dependsOn(core, commons)

lazy val client = Project(
  id = "client",
  base = file("client"))
  .enablePlugins(PackPlugin)
  .settings(commonSettings)
  .settings(name := "client",
    libraryDependencies ++= (
      akkaDependencies ++
        testDependencies ++
      catsDependencies
      )
  ).dependsOn(core, commons)