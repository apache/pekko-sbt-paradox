scalaVersion := "2.13.11"

ThisBuild / apacheSonatypeProjectProfile := "pekko"
ThisBuild / dynverSonatypeSnapshots := true
sourceDistName := "apache-pekko-sbt-paradox"
sourceDistIncubating := true

lazy val publishSettings = Seq(
  startYear := Some(2023),
  developers := List(
    Developer(
      "pekko-sbt-paradox-contributors",
      "Apache Pekko Sbt Paradox Contributors",
      "dev@pekko.apache.org",
      url("https://github.com/apache/incubator-pekko-sbt-paradox/graphs/contributors"))),
  apacheSonatypeDisclaimerFile := Some((LocalRootProject / baseDirectory).value / "DISCLAIMER"))

lazy val pekkoParadox = project
  .in(file("."))
  .settings(
    publish / skip := true)
  .aggregate(pekkoTheme, pekkoPlugin)

lazy val pekkoTheme = project
  .in(file("theme"))
  .enablePlugins(ParadoxThemePlugin, ReproducibleBuildsPlugin)
  .settings(
    name := "pekko-theme-paradox",
    libraryDependencies ++= Seq(
      "io.github.jonas" % "paradox-material-theme" % "0.6.0",
      "org.webjars" % "foundation" % "6.2.4" % "provided"))
  .settings(publishSettings)

lazy val pekkoPlugin = project
  .in(file("plugin"))
  .enablePlugins(SbtPlugin, ReproducibleBuildsPlugin)
  .settings(
    sbtPlugin := true,
    name := "pekko-sbt-paradox",
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    libraryDependencies ++= Seq(
      "io.spray" %% "spray-json" % "1.3.6"),
    addSbtPlugin(
      // When updating the sbt-paradox version,
      // remember to also update project/plugins.sbt
      "com.lightbend.paradox" % "sbt-paradox" % "0.10.3"),
    addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-apidoc" % "1.0.0"),
    addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-project-info" % "3.0.0"),
    addSbtPlugin("io.github.jonas" % "sbt-paradox-material-theme" % "0.6.0"),
    Compile / resourceGenerators += Def.task {
      val file = (Compile / resourceManaged).value / "pekko-paradox.properties"
      IO.write(file, s"pekko.paradox.version=${version.value}")
      Seq(file)
    }).settings(publishSettings)
