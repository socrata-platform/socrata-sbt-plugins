package com.socrata.sbtplugins

import java.io.File.{separator, separatorChar}

/**
 * Convenience class to be able to write a String with '/' in code.
 */
class StringPath(val path: String) {
  def / (part: String): String = path + // scalastyle:ignore method.name
    (if (path.endsWith(separator)) "" else separator) +
    (if (part.startsWith(separator)) part.substring(1) else part)
  def asPath: String = separator + path.replace('.', separatorChar) + separator
}

/**
 * Companion object with implicit conversion
 */
object StringPath {
  implicit def string2StringPath(path: String): StringPath = new StringPath(path)
}
