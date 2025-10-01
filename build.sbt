/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.bzzt.reproduciblebuilds.ReproducibleBuildsPlugin.reproducibleBuildsCheckResolver

val scala212 = "2.12.20"

ThisBuild / scalaVersion := scala212
ThisBuild / crossScalaVersions := Seq(scala212)

ThisBuild / apacheSonatypeProjectProfile := "pekko"
ThisBuild / dynverSonatypeSnapshots := true
sourceDistName := "apache-pekko-sbt-paradox"
sourceDistIncubating := false

commands := commands.value.filterNot { command =>
  command.nameOption.exists { name =>
    name.contains("sonatypeRelease") || name.contains("sonatypeBundleRelease")
  }
}

ThisBuild / reproducibleBuildsCheckResolver :=
  "Apache Pekko Staging".at("https://repository.apache.org/content/groups/staging/")

lazy val publishSettings = Seq(
  startYear := Some(2023),
  developers := List(
    Developer(
      "pekko-sbt-paradox-contributors",
      "Apache Pekko Sbt Paradox Contributors",
      "dev@pekko.apache.org",
      url("https://github.com/apache/pekko-sbt-paradox/graphs/contributors"))))

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
      "com.github.sbt" % "paradox-material-theme" % "0.7.0",
      "org.webjars" % "foundation" % "6.4.3-1" % "provided"))
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
    addSbtPlugin(
      // When updating the sbt-paradox version,
      // remember to also update project/plugins.sbt
      "com.lightbend.paradox" % "sbt-paradox" % "0.10.7"),
    addSbtPlugin("com.github.sbt" % "sbt-web" % "1.5.8"),
    addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-apidoc" % "1.1.0"),
    addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-project-info" % "3.0.1"),
    addSbtPlugin("com.github.sbt" % "sbt-paradox-material-theme" % "0.7.0"),
    Compile / resourceGenerators += Def.task {
      val file = (Compile / resourceManaged).value / "pekko-paradox.properties"
      IO.write(file, s"pekko.paradox.version=${version.value}")
      Seq(file)
    }).settings(publishSettings)

ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test", "scripted")))

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("main")))

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    commands = List("publish"),
    name = Some("Publish project"),
    env = Map(
      "NEXUS_USER" -> "${{ secrets.NEXUS_USER }}",
      "NEXUS_PW" -> "${{ secrets.NEXUS_PW }}")))

ThisBuild / githubWorkflowOSes := Seq("ubuntu-latest", "windows-latest")

ThisBuild / githubWorkflowJavaVersions := Seq(
  JavaSpec.temurin("8"))

ThisBuild / scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-encoding",
  "UTF-8")

// We don't want to depend on sbt-pekko-build since this project
// is not a pekko module, i.e. its just a common theme for document
// generation so the settings should match
// https://github.com/pjfanning/sbt-pekko-build/blob/main/src/main/scala/com/github/pjfanning/pekkobuild/PekkoInlinePlugin.scala#L27-L30

lazy val pekkoInlineEnabled: SettingKey[Boolean] = settingKey(
  "Whether to enable the Scala 2 inliner Defaults to pekko.no.inline property")

pekkoInlineEnabled := {
  val prop = "pekko.no.inline"
  val enabled = !sys.props.contains(prop)
  val log = sLog.value
  if (enabled)
    log.info(s"Scala 2 optimizer/inliner enabled, to disable set the $prop system property")
  else
    log.info(s"Scala 2 optimizer/inliner disabled, to enable remove the $prop system property")
  enabled
}

ThisBuild / scalacOptions ++= {
  if (pekkoInlineEnabled.value) {
    List(
      "-opt-inline-from:<sources>",
      "-opt:l:inline")
  } else List.empty
}
