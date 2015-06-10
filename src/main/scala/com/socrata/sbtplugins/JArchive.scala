package com.socrata.sbtplugins

import java.io.{File, InputStream}
import java.net.URL

import sbt._

import scala.language.implicitConversions
import scala.util.{Success, Failure, Try}

class JArchive(val url: URL) {
  def getFileFromJar(target: File, logger: Logger): File = {
    val successMsg = "created: %s"

    implicit def enumToIterator[A](e: java.util.Enumeration[A]): Iterator[A] = new Iterator[A] {
      def next(): A = e.nextElement
      def hasNext: Boolean = e.hasMoreElements
    }

    def transfer(iStream: InputStream, target: File): Unit = {
      IO.transfer(iStream, target)
      iStream.close()
      logger.success(successMsg.format(target))
    }

    Try {
      url.openConnection match {
        case connection: java.net.JarURLConnection =>
          val entryName = connection.getEntryName
          val jarFile = connection.getJarFile
          jarFile.entries.filter(_.getName == entryName).foreach(e =>
            transfer(jarFile.getInputStream(e), target)
          )
        case connection: java.net.HttpURLConnection => logger.error("http connection type not implemented")
        case connection: sun.net.www.protocol.file.FileURLConnection =>
          transfer(connection.getInputStream, target)
        case c: Any => logger.error("unknown connection type %s".format(c.toString))
      }
    } match {
      case Failure(e: java.io.IOException) => logger.error(e.getMessage)
      case Failure(e: Throwable) => throw e
      case Success(_) => ()
    }

    target
  }
}
