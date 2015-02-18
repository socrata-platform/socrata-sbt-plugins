package com.socrata.sbtplugins

import java.io.File.{pathSeparator, pathSeparatorChar}

/**
 * Convenience class to be able to write a String with '/' in code.
 */
object StringPath {
  class StringPath(val path: String) {
    def / (part: String): String = path + // scalastyle:ignore method.name
      (if (path.endsWith(pathSeparator)) "" else pathSeparator) +
      (if (part.startsWith(pathSeparator)) part.substring(1) else part)
    def asPath: String = pathSeparator + path.replace('.', pathSeparatorChar)
  }
  implicit def string2StringPath(path: String): StringPath = new StringPath(path)
}
