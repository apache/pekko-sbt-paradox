// Copied and adapted from https://github.com/sbt/sbt-paradox-material-theme/blob/2d57fe0567ea9fe7e8de14faef4fa777841d505a/plugin/src/main/scala/io.github.jonas.paradox.material.theme/SearchIndex.scala

package org.apache.pekko

import scala.collection.JavaConverters._
import com.lightbend.paradox.sbt.ParadoxPlugin.autoImport.paradoxMarkdownToHtml
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import sbt._
import sbt.Keys._
import spray.json._

case class SearchIndex(docs: Seq[SearchIndex.Section])

object SearchIndex {
  case class Section(location: String, title: String, text: String)
  implicit val encoder: JsonFormat[SearchIndex] = {
    import DefaultJsonProtocol._

    implicit val sectionFormat: JsonFormat[Section] =
      jsonFormat3(Section.apply _)

    jsonFormat1(SearchIndex.apply _)
  }

  val Headers = Set("h1", "h2", "h3", "h4", "h5", "h6")

  def generate(target: File, mappings: Seq[(File, String)]): File = {
    def readSections(mapping: (File, String)): Seq[Section] = {
      val (file, location) = mapping
      val doc = Jsoup.parse(file, "UTF-8")
      val docTitle = {
        val title = doc.select("head title").text()
        Option(title.lastIndexOf(" · "))
          .filter(_ > 0)
          .map(title.substring(0, _))
          .getOrElse(title)
      }

      def headerLocation(header: Element) =
        Option(header.select("a[name]").first()) match {
          case Some(anchor) => location + "#" + anchor.attr("name")
          case None         => location
        }

      def processElement(section: Section, elements: List[Element]): Seq[Section] =
        elements match {
          case header :: tail if Headers(header.tagName) =>
            val location = headerLocation(header)
            val next = Section("/" + location, header.text, "")
            Vector(section) ++ processElement(next, tail)
          case element :: tail =>
            val text =
              if (section.text.isEmpty) element.text
              else section.text + "\n" + element.text
            processElement(section.copy(text = text.trim), tail)
          case Nil =>
            Vector(section)
        }

      val elements =
        doc.select("body .md-content__searchable").asScala.flatMap(_.children.asScala).toList

      processElement(Section("/" + location, docTitle, ""), elements)
    }

    val sections = mappings.flatMap(readSections).toList
    val searchIndex = SearchIndex(sections)
    val json = searchIndex.toJson.compactPrint
    val out = target / "search_index.json"
    IO.write(out, json)
    out
  }

  def mapping(config: Configuration) = Def.task {
    val index = generate(
      (config / target).value / "paradox-material-theme",
      (config / paradoxMarkdownToHtml).value)
    index -> "search/search_index.json"
  }
}
