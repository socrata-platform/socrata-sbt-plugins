package com.socrata.sbtplugins

import sbt._
import sbt.Keys._

/** A simple AutoPlugin for initial testing.
  *
  * To exercise this plugin, run the command ```sbt hello```.
  * Copied from [[http://www.scala-sbt.org/0.13/docs/Plugins.html]]
  */
object HelloWorldPlugin extends AutoPlugin {
  /** When to enable this autoplugin.
    * @return On all requirements in all scopes. */
  override def trigger: PluginTrigger = allRequirements

  /** Settings for the build scope.
    * @return Settings to import in build scope. */
  override lazy val buildSettings = Seq(commands += helloCommand)

  /** Trivial sbt command. */
  lazy val helloCommand =
    Command.command("hello") { (state: State) =>
      println("Hi!") //scalastyle:ignore
      state
    }
}
