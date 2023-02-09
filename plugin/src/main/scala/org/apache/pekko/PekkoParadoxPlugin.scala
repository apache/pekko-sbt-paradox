package org.apache.pekko

import com.lightbend.paradox.sbt.ParadoxPlugin
import com.typesafe.sbt.web.Import.{ Assets, WebKeys }
import io.github.jonas.paradox.material.theme.ParadoxMaterialThemePlugin
import io.github.jonas.paradox.material.theme.ParadoxMaterialThemePlugin.autoImport.*
import sbt.*
import sbt.Keys.*

object PekkoParadoxPlugin extends AutoPlugin {

  import ParadoxPlugin.autoImport._

  val version = ParadoxPlugin.readProperty("pekko-paradox.properties", "pekko.paradox.version")

  override def requires = ParadoxPlugin

  override def trigger = noTrigger

  override def projectSettings: Seq[Setting[_]] =
    ParadoxMaterialThemePlugin.projectSettings ++ pekkoParadoxSettings(Compile)

  def pekkoParadoxGlobalSettings: Seq[Setting[_]] = Seq(
    paradoxTheme := Some("org.apache.pekko" % "pekko-theme-paradox" % version),
    paradoxNavigationDepth := 1,
    paradoxNavigationExpandDepth := Some(1),
    paradoxNavigationIncludeHeaders := true,
    Compile / paradoxMaterialTheme ~= {
      _
        .withLogo("assets/images/pekko_logo.png")
        .withFavicon("assets/images/pekko_favicon.png")
        .withCopyright(
          """Copyright © 2022, 2023 <a href="https://apache.org">The Apache Software Foundation</a>, Licensed under the Apache License, Version 2.0.
            | This product contains significant parts that were originally based on software from Lightbend (<a href="https://akka.io/">Akka</a>).
            | Copyright (C) 2009-2022 Lightbend Inc. &lt;https://www.lightbend.com&gt; Apache Pekko is derived from Akka 2.6.x,
            | the last version that was distributed under the Apache License, Version 2.0 License.""".stripMargin) // FIXME: figure out exact text
    })

  def pekkoParadoxSettings(config: Configuration): Seq[Setting[_]] = pekkoParadoxGlobalSettings ++ inConfig(config)(Seq(
    managedSourceDirectories in paradoxTheme +=
      (WebKeys.webJarsDirectory in Assets).value / (WebKeys.webModulesLib in Assets).value / "paradox-material-theme"))
}
