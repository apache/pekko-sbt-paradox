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

import java.io.File

import scala.collection.mutable
import scala.util.matching.Regex

import sbt.IO

/**
 * Paradox derives a heading's anchor from the heading text only, so a page that repeats a heading
 * (e.g. "Bug Fixes" under each patch release in release notes) gets the same anchor several times
 * and all of those links go to the first heading.
 *
 * This renames the 2nd and later duplicate header anchors on a page to `<anchor>-1`, `<anchor>-2`, ...
 * (like GitHub does), keeping the first one unchanged so existing links still work.
 * Other links to a duplicated anchor are left as they are and keep pointing at the first heading.
 */
private[pekko] object UniqueHeaderAnchors {
  private val HeaderAnchor: Regex = """<a href="#([^"]+)" name="\1" class="anchor">""".r

  /** Returns `html` itself (same instance) when it has no duplicate header anchors. */
  def rewrite(html: String): String = {
    val names = HeaderAnchor.findAllMatchIn(html).map(_.group(1)).toVector
    if (names.distinct.size == names.size) html
    else {
      val used = mutable.Set(names: _*)
      // last suffix tried per name, so the k-th duplicate doesn't re-check -1 .. -(k-1)
      val lastSuffix = mutable.Map.empty[String, Int]
      HeaderAnchor.replaceAllIn(html,
        m => {
          val name = m.group(1)
          val unique = lastSuffix.get(name) match {
            case None =>
              lastSuffix(name) = 0
              name
            case Some(last) =>
              var i = last + 1
              while (used(s"$name-$i")) i += 1
              lastSuffix(name) = i
              val n = s"$name-$i"
              used += n
              n
          }
          Regex.quoteReplacement(s"""<a href="#$unique" name="$unique" class="anchor">""")
        })
    }
  }

  /** Rewrites the given HTML files in place, leaving files without duplicate anchors untouched. */
  def rewriteFiles(files: Seq[File]): Unit =
    files.filter(_.getName.endsWith(".html")).foreach { file =>
      val html = IO.read(file)
      val fixed = rewrite(html)
      if (fixed ne html) IO.write(file, fixed)
    }
}
