package com.socrata.sbtplugins

/**
 * Convenience class to be able to write a String with '/' in code.
 */
object StringPath {
  val SLASH: String = "/"
  class StringPath(val path: String) {
    def / (part: String): String = path + // scalastyle:ignore method.name
      (if (path.endsWith(SLASH)) "" else SLASH) +
      (if (part.startsWith(SLASH)) part.substring(1) else part)
    def asPath: String = SLASH + path.replace('.', '/')
  }
  implicit def string2StringPath(path: String): StringPath = new StringPath(path)
}
