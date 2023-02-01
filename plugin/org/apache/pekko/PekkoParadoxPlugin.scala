package org.apache.pekko

import sbt._
import sbt.Keys._
import com.lightbend.paradox.markdown.{ ContainerBlockDirective, Directive, Writer }
import com.lightbend.paradox.sbt.ParadoxPlugin
import org.pegdown.Printer
import org.pegdown.ast.{ DirectiveNode, Visitor }

object PekkoParadoxPlugin extends AutoPlugin {

  import ParadoxPlugin.autoImport._

  val version = ParadoxPlugin.readProperty("pekko-paradox.properties", "pekko.paradox.version")

  override def requires = ParadoxPlugin

  override def trigger = noTrigger

  override def projectSettings: Seq[Setting[_]] = pekkoParadoxSettings(Compile)

  def pekkoParadoxGlobalSettings: Seq[Setting[_]] = Seq(
    paradoxTheme := Some("org.apache.pekko" % "pekko-theme-paradox" % version),
    paradoxNavigationDepth := 1,
    paradoxNavigationExpandDepth := Some(1),
    paradoxNavigationIncludeHeaders := true,
    paradoxDirectives += SidenoteDirective)

  def pekkoParadoxSettings(config: Configuration): Seq[Setting[_]] = pekkoParadoxGlobalSettings ++ inConfig(config)(Seq(
    // scoped settings here
  ))

  object SidenoteDirective extends ContainerBlockDirective("sidenote") with (Writer.Context => Directive) {
    def apply(context: Writer.Context): Directive = SidenoteDirective

    def render(node: DirectiveNode, visitor: Visitor, printer: Printer): Unit = {
      val classes = node.attributes.classesString
      printer.print(s"""<div class="sidenote $classes">""")
      node.contentsNode.accept(visitor)
      printer.print("""</div>""")
    }
  }

}
