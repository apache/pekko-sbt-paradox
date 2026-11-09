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

class UniqueHeaderAnchorsSpec extends munit.FunSuite {

  private def anchor(name: String) = s"""<a href="#$name" name="$name" class="anchor">"""
  private def h3(name: String) = s"""<h3>${anchor(name)}<span class="anchor-link"></span></a>Title</h3>"""

  test("leave a page without duplicate anchors untouched") {
    val html = h3("changes") + h3("bug-fixes")
    assert(UniqueHeaderAnchors.rewrite(html) eq html)
  }

  test("suffix the 2nd and later duplicates, keeping the first anchor") {
    val html = h3("bug-fixes") + h3("changes") + h3("bug-fixes") + h3("bug-fixes")
    assertEquals(
      UniqueHeaderAnchors.rewrite(html),
      h3("bug-fixes") + h3("changes") + h3("bug-fixes-1") + h3("bug-fixes-2"))
  }

  test("skip suffixes already used by another heading") {
    val html = h3("bug-fixes") + h3("bug-fixes-1") + h3("bug-fixes")
    assertEquals(
      UniqueHeaderAnchors.rewrite(html),
      h3("bug-fixes") + h3("bug-fixes-1") + h3("bug-fixes-2"))
  }

  test("leave other links to a duplicated anchor alone") {
    val link = """<a href="#bug-fixes">see bug fixes</a>"""
    val html = link + h3("bug-fixes") + h3("bug-fixes")
    assertEquals(UniqueHeaderAnchors.rewrite(html), link + h3("bug-fixes") + h3("bug-fixes-1"))
  }
}
