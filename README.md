<!--
  ~ Licensed to the Apache Software Foundation (ASF) under one or more
  ~ contributor license agreements.  See the NOTICE file distributed with
  ~ this work for additional information regarding copyright ownership.
  ~ The ASF licenses this file to You under the Apache License, Version 2.0
  ~ (the "License"); you may not use this file except in compliance with
  ~ the License.  You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  -->
  
# Pekko SBT Paradox

This plugin is intended to extend the [sbt-paradox](https://github.com/lightbend/paradox) plugin with some settings
along with providing the Apache Pekko theme.

It is intended as a shared doc theme for the [Apache Pekko](https://github.com/apache/?q=pekko&type=all&language=&sort=)
umbrella of projects, and not as a public theme to use on "any" project.

## Usage

### JDK 1.8 (only)

The project intentionally supports JDK 1.8 by default since its built against
[sbt-paradox/sbt-paradox-theme 0.9.2](https://github.com/apache/incubator-pekko-sbt-paradox/blob/main/project/plugins.sbt#L20-L21)
so you can just include it like this

```sbt
addSbtPlugin("org.apache.pekko" % "sbt-paradox-pekko" % "<version>")
```

### JDK 11+

With JDK 11 or later you need to explicitly override the sbt-paradox/sbt-paradox-theme
versions to use the 10.6.x (or newer) series, i.e.

```sbt
addSbtPlugin("org.apache.pekko" % "sbt-paradox-pekko" % "<version>")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.10.6")
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-theme" % "0.10.6")
```

Enable it instead of the upstream ParadoxPlugin:
```sbt
import org.apache.pekko.PekkoParadoxPlugin

enablePlugins(PekkoParadoxPlugin)
```

For more settings refer to the [Paradox documentation](https://developer.lightbend.com/docs/paradox/latest/)

## Testing

When you have made changes to pekko-sbt-paradox, you can test it locally with:

```
sbt publishLocal sbtPlugin/scripted
```

This should show:

```
Pausing in /tmp/sbt_e457458e/simple
Press enter to continue.
```

## Building from Source

### Prerequisites
- Make sure you have installed a Java Development Kit (JDK) version 8.
- Make sure you have [sbt](https://www.scala-sbt.org/) installed and using this JDK.

### Running the Build
- Open a command window and change directory to your preferred base directory
- Use git to clone the [repo](https://github.com/apache/incubator-pekko-sbt-paradox) or download a source release from https://pekko.apache.org (and unzip or untar it, as appropriate)
- Change directory to the directory where you installed the source (you should have a file called `build.sbt` in this directory)
- `sbt compile` compiles the main source for project default version of Scala (2.13)
- `sbt test` will compile the code and run the unit tests
- `sbt package` will build the jars
    - the jars will built into target dirs of the various modules
    - for the the 'plugin' module, the jar will be built to `plugin/target/scala-2.12/sbt-1.0/`
- `sbt publishLocal` will push the jars to your local Apache Ivy repository
- `sbt publishM2` will push the jars to your local Apache Maven repository
- `sbt sourceDistGenerate` will generate source release to `target/dist/`
- The version number that appears in filenames and docs is derived, by default. The derived version contains the most git commit id or the date/time (if the directory is not under git control). 
    - You can set the version number explicitly when running sbt commands
        - eg `sbt "set ThisBuild / version := \"1.0.0\"; sourceDistGenerate"`  
    - Or you can add a file called `version.sbt` to the same directory that has the `build.sbt` containing something like
        - `ThisBuild / version := "1.0.0"` 

## Community

There are several ways to interact with the Pekko community:

- [GitHub discussions](https://github.com/apache/incubator-pekko/discussions): for questions and general discussion.
- [Pekko dev mailing list](https://lists.apache.org/list.html?dev@pekko.apache.org): for Pekko development discussions.
- [Pekko users mailing list](https://lists.apache.org/list.html?users@pekko.apache.org): for Pekko user discussions.
- [GitHub issues](https://github.com/apache/incubator-pekko-sbt-paradox/issues): for bug reports and feature requests. Please search the existing issues before creating new ones. If you are unsure whether you have found a bug, consider asking in GitHub discussions or the mailing list first.
