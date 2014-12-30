package com.socrata.sbtplugins

import sbt._
import sbt.Keys._

// Copied from http://www.scala-sbt.org/0.13/docs/Plugins.html
// To exercise this plugin, run the command "sbt hello" in the test-project directory
object HelloWorldPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override lazy val buildSettings = Seq(commands += helloCommand)
  lazy val helloCommand =
    Command.command("hello") { (state: State) =>
      println("Hi!") //scalastyle:ignore
      state
    }
}
