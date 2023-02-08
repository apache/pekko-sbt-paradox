scalaVersion := "2.13.10"

licenses += "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")

lazy val apacheBaseRepo = "repository.apache.org"

def apacheNexusCredentials: Seq[Credentials] =
  (sys.env.get("NEXUS_USER"), sys.env.get("NEXUS_PW")) match {
    case (Some(user), Some(password)) =>
      Seq(Credentials("Sonatype Nexus Repository Manager", apacheBaseRepo, user, password))
    case _ =>
      Seq.empty
  }

lazy val publishSettings = Seq(
  credentials ++= apacheNexusCredentials,
  organizationName := "Apache Software Foundation",
  organizationHomepage := Some(url("https://www.apache.org")),
  sonatypeCredentialHost := apacheBaseRepo,
  sonatypeProfileName := "org.apache.pekko",
  startYear := Some(2022),
  developers := List(
    Developer(
      "pekko-contributors",
      "Apache Pekko Contributors",
      "dev@pekko.apache.org",
      url("https://github.com/apache/incubator-pekko/graphs/contributors"))),
  publishMavenStyle := true,
  pomIncludeRepository := (_ => false))

lazy val pekkoParadox = project
  .in(file("."))
  .settings(
    publish / skip := true)
  .aggregate(pekkoTheme, pekkoPlugin)

lazy val pekkoTheme = project
  .in(file("theme"))
  .enablePlugins(ParadoxThemePlugin)
  .settings(
    organization := "org.apache.pekko",
    name := "pekko-theme-paradox",
    libraryDependencies += "io.github.jonas" % "paradox-material-theme" % "0.6.0")
  .settings(publishSettings)

lazy val pekkoPlugin = project
  .in(file("plugin"))
  .enablePlugins(SbtPlugin)
  .settings(
    sbtPlugin := true,
    organization := "org.apache.pekko",
    name := "pekko-sbt-paradox",
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    addSbtPlugin(
      // When updating the sbt-paradox version,
      // remember to also update project/plugins.sbt
      "com.lightbend.paradox" % "sbt-paradox" % "0.9.2"),
    addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-apidoc" % "0.10.1"),
    addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-project-info" % "2.0.0"),
    addSbtPlugin("io.github.jonas" % "sbt-paradox-material-theme" % "0.6.0"),
    Compile / resourceGenerators += Def.task {
      val file = (Compile / resourceManaged).value / "pekko-paradox.properties"
      IO.write(file, s"pekko.paradox.version=${version.value}")
      Seq(file)
    }).settings(publishSettings)
