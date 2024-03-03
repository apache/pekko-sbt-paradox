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

// When updating the sbt-paradox version,
// remember to also update build.sbt
addSbtPlugin(("com.lightbend.paradox" % "sbt-paradox-theme" % "0.9.2").exclude("com.typesafe.sbt", "sbt-web"))
addSbtPlugin(("com.lightbend.paradox" % "sbt-paradox" % "0.9.2").exclude("com.typesafe.sbt", "sbt-web"))
addSbtPlugin("com.github.sbt" % "sbt-web" % "1.5.5") // sbt-paradox[-theme] 0.9.2 depends on old sbt-web 1.4.x, but we want a newer version
addSbtPlugin("com.github.sbt" % "sbt-dynver" % "5.0.1")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")
addSbtPlugin("org.mdedetrich" % "sbt-apache-sonatype" % "0.1.11")
addSbtPlugin("com.github.pjfanning" % "sbt-source-dist" % "0.1.12")
addSbtPlugin("net.bzzt" % "sbt-reproducible-builds" % "0.32")
addSbtPlugin("com.github.sbt" % "sbt-github-actions" % "0.23.0")
