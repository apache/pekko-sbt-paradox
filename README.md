# Pekko SBT Paradox

This plugin is intended to extend the [sbt-paradox](https://github.com/lightbend/paradox) plugin with some settings
along with providing the Apache Pekko theme.

It is intended as a shared doc theme for the [Apache Pekko](https://github.com/apache/?q=pekko&type=all&language=&sort=)
umbrella of projects, and not as a public theme to use on "any" project.

This project is largely based on the original [akka-paradox](https://github.com/akka/akka-paradox) plugin. Credits go
to the original [contributors](https://github.com/akka/akka-paradox/graphs/contributors) of the project.

## Usage

Use the sbt plugin for Pekko Paradox:
```sbt
addSbtPlugin("org.apache.pekko" % "pekko-sbt-paradox" % "<version>")
```

### Usage within JDK 1.8 projects

Due to the design of certain transitive dependencies used by sbt-paradox such as parboiled, this plugin won't
work correctly if run under JDK 1.8

You can work around this by hotpatching the resolved dependencies, rather than doing the simple `addSbtPlugin`
as mentioned before do the following

```sbt
addSbtPlugin("org.apache.pekko" % "sbt-paradox-pekko" % "<version>" excludeAll(
  "com.lightbend.paradox" % "sbt-paradox",
  "com.lightbend.paradox" % "sbt-paradox-apidoc",
  "com.lightbend.paradox" % "sbt-paradox-project-info"
))
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox" % "0.9.2" force())
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-apidoc" % "0.10.1" force())
addSbtPlugin("com.lightbend.paradox" % "sbt-paradox-project-info" % "2.0.0" force())
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
