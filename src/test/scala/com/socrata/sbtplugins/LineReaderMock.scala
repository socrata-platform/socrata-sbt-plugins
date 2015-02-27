package com.socrata.sbtplugins

import sbt.LineReader

class LineReaderMock(os: Seq[String]) extends LineReader {
  var stack = os
  var asks = 0

  override def readLine(prompt: String, mask: Option[Char]): Option[String] = {
    asks += 1
    stack match {
      case h :: t =>
        stack = t
        Some(h)
      case _ => None
    }
  }
}
