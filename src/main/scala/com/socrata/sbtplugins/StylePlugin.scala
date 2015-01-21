package com.socrata.sbtplugins

import java.io.{FileOutputStream, File}
import java.net.URL

import sbt._
import sbt.Keys._
import org.scalastyle.sbt.{ScalastylePlugin, Tasks => ScalastyleTasks}

import scala.language.implicitConversions

object StylePlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Setting[_]] =
    ScalastylePlugin.projectSettings ++
    inConfig(Compile)(configSettings) ++
    inConfig(Test)(configSettings) ++ Seq(
      (StyleKeys.styleConfigName in Compile) := "/scalastyle-config.xml",
      (StyleKeys.styleConfigName in Test) := "/scalastyle-test-config.xml",
      (StyleKeys.styleResultName in Compile) := "/scalastyle-result.xml",
      (StyleKeys.styleResultName in Test) := "/scalastyle-test-result.xml",
      (test in Test) <<= (test in Test) dependsOn (StyleKeys.styleCheck in Test),
      (Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn (StyleKeys.styleCheck in Compile)
    )

  def configSettings: Seq[Setting[_]] = Seq(
    StyleKeys.styleCheck := {
      val args = Seq()
      val configXml = getFileFromJar(
        state.value,
        getClass.getResource(StyleKeys.styleConfigName.value),
        target.value / StyleKeys.styleConfigName.value)
      val warnIsError = true
      val sourceDir = (scalaSource in StyleKeys.styleCheck).value
      val outputXml = target.value / StyleKeys.styleResultName.value
      val localStreams = streams.value
      val configRefreshHours = 0
      ScalastyleTasks.doScalastyle(
        args,
        configXml,
        None,
        warnIsError,
        sourceDir,
        outputXml,
        localStreams,
        configRefreshHours,
        target.value,
        "/dev/null"
      )
    }
  )

  object StyleKeys {
    val styleCheck = TaskKey[Unit]("styleCheck", "Check scala source files using scalastyle")
    val styleConfigName = SettingKey[String]("styleConfigName", "scalastyle config file")
    val styleResultName = SettingKey[String]("styleResultName", "scalastyle result file")
  }

  private def getFileFromJar(state: State, url: URL, target: File): File = {
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
            state.log.success(successMsg.format(target))
          })
        case connection: java.net.HttpURLConnection => state.log.error("http connection type not implemented")
        case connection: sun.net.www.protocol.file.FileURLConnection =>
          val iStream = connection.getInputStream
          val oStream = new FileOutputStream(target)
          Iterator.continually(iStream.read)
            .takeWhile(_ != -1)
            .foreach(oStream.write)
          iStream.close()
          oStream.close()
          state.log.success(successMsg.format(target))
        case c => state.log.error("unknown connection type %s".format(c.toString))
      }
    } catch {
      case e: java.io.IOException => state.log.error(e.getMessage)
    }

    target
  }
}
