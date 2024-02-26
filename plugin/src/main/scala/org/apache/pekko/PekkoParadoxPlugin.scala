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

package org.apache.pekko

import com.lightbend.paradox.sbt.ParadoxPlugin
import com.typesafe.sbt.web.Import.{ Assets, WebKeys }
import com.github.sbt.paradox.material.theme.ParadoxMaterialThemePlugin
import com.github.sbt.paradox.material.theme.ParadoxMaterialThemePlugin.autoImport._
import sbt._
import sbt.Keys._

object PekkoParadoxPlugin extends AutoPlugin {

  import ParadoxPlugin.autoImport._

  object autoImport {
    val pekkoParadoxCopyright = settingKey[String]("Copyright text to use in docs footer")
    val pekkoParadoxGithub = settingKey[Option[String]]("Link to Github repository")
    val pekkoParadoxIncubatorNotice = settingKey[Option[String]]("Whether to include the ASF incubator notice")
  }
  import autoImport._

  val version = ParadoxPlugin.readProperty("pekko-paradox.properties", "pekko.paradox.version")

  val incubatorNoticeText =
    "Apache Pekko is an effort undergoing incubation at The Apache Software Foundation (ASF), sponsored by the Apache Incubator. Incubation is required of all newly accepted projects until a further review indicates that the infrastructure, communications, and decision making process have stabilized in a manner consistent with other successful ASF projects. While incubation status is not necessarily a reflection of the completeness or stability of the code, it does indicate that the project has yet to be fully endorsed by the ASF."

  override lazy val requires = ParadoxPlugin

  override lazy val trigger = noTrigger

  override lazy val projectSettings: Seq[Setting[_]] =
    ParadoxMaterialThemePlugin.projectSettings ++ pekkoParadoxSettings(Compile)

  lazy val pekkoParadoxGlobalSettings: Seq[Setting[_]] = Seq(
    paradoxTheme := Some("org.apache.pekko" % "pekko-theme-paradox" % version),
    // Target hostname for static assets (CSS, JS, Icons, Font)
    paradoxProperties ++= {
      Map("assets.hostname" -> "https://pekko.apache.org/") ++
      pekkoParadoxIncubatorNotice.value.map(incubatorNotice => Map("incubator.notice" -> incubatorNotice)).getOrElse(
        Map.empty)
    },
    paradoxNavigationIncludeHeaders := true,
    pekkoParadoxCopyright in Global :=
      """Copyright © 2011-2022 <a href="https://www.lightbend.com/">Lightbend, Inc</a>.
        | Apache Pekko, Pekko, and its feather logo are trademarks of The Apache Software Foundation.""".stripMargin,
    pekkoParadoxGithub in Global := None,
    pekkoParadoxIncubatorNotice in Global := Some(incubatorNoticeText),
    Compile / paradoxMaterialTheme := {
      val theme =
        (Compile / paradoxMaterialTheme).value
          .withLogo("assets/images/pekko_logo.png")
          .withFavicon("assets/images/pekko_favicon.png")
          .withCustomStylesheet("assets/stylesheets/pekko-theme.css")
          .withColor("white", "orange")
          .withCopyright(pekkoParadoxCopyright.value)

      pekkoParadoxGithub.value match {
        case Some(githubUrl) => theme.withRepository(uri(githubUrl))
        case None            => theme
      }
    })

  def pekkoParadoxSettings(config: Configuration): Seq[Setting[_]] = pekkoParadoxGlobalSettings ++ inConfig(config)(Seq(
    paradoxTheme / managedSourceDirectories +=
      (Assets / WebKeys.webJarsDirectory).value / (Assets / WebKeys.webModulesLib).value / "paradox-material-theme",
    // we override some files from paradox-material-theme, so we must solve the ambiguity where to take those duplicates from
    paradoxTheme / WebKeys.deduplicators += { (files: Seq[File]) =>
      files.find(_.getPath.contains("pekko-theme-paradox"))
    }))
}
