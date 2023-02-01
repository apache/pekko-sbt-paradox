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

Enable it instead of the upstream ParadoxPlugin:
```sbt
import org.apache.pekko.PekkoParadoxPlugin

enablePlugins(PekkoParadoxPlugin)
```

For more settings refer to the [Paradox documentation](https://developer.lightbend.com/docs/paradox/latest/)

## Testing

When you have made changes to akka-paradox, you can test it locally with:

```
sbt publishLocal sbtPlugin/scripted
```

This should show:

```
Pausing in /tmp/sbt_e457458e/simple
Press enter to continue.
```
