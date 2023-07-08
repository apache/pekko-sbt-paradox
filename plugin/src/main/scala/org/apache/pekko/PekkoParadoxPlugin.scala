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
import io.github.jonas.paradox.material.theme.ParadoxMaterialThemePlugin
import io.github.jonas.paradox.material.theme.ParadoxMaterialThemePlugin.autoImport._
import sbt._
import sbt.Keys._

object PekkoParadoxPlugin extends AutoPlugin {

  import ParadoxPlugin.autoImport._

  object autoImport {
    val pekkoParadoxCopyright = settingKey[String]("Copyright text to use in docs footer")
    val pekkoParadoxGithub = settingKey[Option[String]]("Link to Github repository")
  }
  import autoImport._

  val version = ParadoxPlugin.readProperty("pekko-paradox.properties", "pekko.paradox.version")

  override def requires = ParadoxPlugin

  override def trigger = noTrigger

  override def projectSettings: Seq[Setting[_]] =
    ParadoxMaterialThemePlugin.projectSettings ++ pekkoParadoxSettings(Compile)

  def pekkoParadoxGlobalSettings: Seq[Setting[_]] = Seq(
    paradoxTheme := Some("org.apache.pekko" % "pekko-theme-paradox" % version),
    paradoxNavigationIncludeHeaders := true,
    pekkoParadoxCopyright in Global :=
      """Copyright © 2011-2022 <a href="https://www.lightbend.com/">Lightbend, Inc</a>.
        | Apache Pekko, Pekko, and its feather logo are trademarks of The Apache Software Foundation.""".stripMargin,
    pekkoParadoxGithub in Global := None,
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
    paradoxMaterialTheme / mappings := Def.taskDyn {
      if (paradoxProperties.value.contains("material.search"))
        Def.task(Seq(org.apache.pekko.SearchIndex.mapping(config).value))
      else
        Def.task(Seq.empty[(File, String)])
    }.value,
    // we override some files from paradox-material-theme, so we must solve the ambiguity where to take those duplicates from
    paradoxTheme / WebKeys.deduplicators += { (files: Seq[File]) =>
      files.find(_.getPath.contains("pekko-theme-paradox"))
    }))
}
