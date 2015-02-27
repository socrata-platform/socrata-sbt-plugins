package com.socrata.sbtplugins

import java.io.{File, FileOutputStream}
import java.net.URL

import sbt._

class JArchive(val url: URL) {
  def getFileFromJar(target: File, logger: Logger): File = {
    val successMsg = "created: %s"

    implicit def enumToIterator[A](e: java.util.Enumeration[A]): Iterator[A] = new Iterator[A] {
      def next(): A = e.nextElement
      def hasNext: Boolean = e.hasMoreElements
    }

    try {
      url.openConnection match {
        case connection: java.net.JarURLConnection =>
          val entryName = connection.getEntryName
          val jarFile = connection.getJarFile
          jarFile.entries.filter(_.getName == entryName).foreach(e => {
            val iStream = jarFile.getInputStream(e)
            IO.transfer(iStream, target)
            iStream.close()
            logger.success(successMsg.format(target))
          })
        case connection: java.net.HttpURLConnection => logger.error("http connection type not implemented")
        case connection: sun.net.www.protocol.file.FileURLConnection =>
          val iStream = connection.getInputStream
          val oStream = new FileOutputStream(target)
          Iterator.continually(iStream.read)
            .takeWhile(_ != -1)
            .foreach(oStream.write)
          iStream.close()
          oStream.close()
          logger.success(successMsg.format(target))
        case c: Any => logger.error("unknown connection type %s".format(c.toString))
      }
    } catch {
      case e: java.io.IOException => logger.error(e.getMessage)
    }

    target
  }
}
