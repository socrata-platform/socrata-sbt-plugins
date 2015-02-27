package com.socrata.sbtplugins

import sbt.{Logger, Level}

class TestLogger extends Logger {
  var lastMessage = ""
  var lastThrown: Option[Throwable] = None
  override def log(level: Level.Value, message: => String): Unit = {
    lastMessage = message
    println(s"  $level:: $message") // scalastyle:ignore
  }
  override def success(message: => String): Unit = this.log(Level.Info, message)
  override def trace(t: => Throwable): Unit = {
    lastThrown = Some(t)
    this.log(Level.Error, t.getMessage)
  }
}
