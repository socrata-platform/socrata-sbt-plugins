package com.socrata.sbtplugins

import java.io.{FileOutputStream, File}
import java.net.URL
import java.util.jar.{JarEntry, JarFile}

import sbt._

object ScalastyleTask {
  def doScalastyle(state: State,
                   scalastyleConfigName: String,
                   scalastyleTargetName: String,
                   scalaSource: File,
                   scalaTarget: File): Unit = {
    val configSrc = getClass.getResource(scalastyleConfigName)
    val configDst = scalaTarget / scalastyleConfigName
    val resultDst = scalaTarget / scalastyleTargetName
    getFileFromJar(state, configSrc, configDst)
    org.scalastyle.sbt.Tasks.doScalastyle(state,
      scalaSource, configDst, None, true, resultDst, 1, scalaTarget, scalastyleConfigName)
  }

  private def getFileFromJar(state: State, url: URL, target: File): Unit = {
    implicit def enumToIterator[A](e: java.util.Enumeration[A]): Iterator[A] = new Iterator[A] {
      def next: A = e.nextElement
      def hasNext: Boolean = e.hasMoreElements
    }

    def createFile(jarFile: JarFile, e: JarEntry, target: File): Unit = {
      IO.transfer(jarFile.getInputStream(e), target)
      state.log.success("created: " + target)
    }

    def safeToCreateFile(file: File): Boolean = {
      def askUser: Boolean = {
        val question = "The file %s exists, do you want to overwrite it? (y/n): ".format(file.getPath)
        scala.Console.readLine(question).toLowerCase.headOption match {
          case Some('y') => true
          case Some('n') => false
          case _ => askUser
        }
      }
      if (file.exists) askUser else true
    }

    if (safeToCreateFile(target)) {
      url.openConnection match {
        case connection: java.net.JarURLConnection => {
          val entryName = connection.getEntryName
          val jarFile = connection.getJarFile
          jarFile.entries.filter(_.getName == entryName).foreach {e => createFile(jarFile, e, target)}
        }
        case connection: java.net.HttpURLConnection => state.log.warn("http connection type")
        case connection: sun.net.www.protocol.file.FileURLConnection => {
          val istream = connection.getInputStream
          val ostream = new FileOutputStream(target)
          Iterator.continually(istream.read)
                  .takeWhile(_ != -1)
                  .foreach(ostream.write)
        }
        case c => state.log.warn("unknown connection type %s".format(c.toString))
      }
    } else {
      state.log.warn("config not created")
    }
  }
}
