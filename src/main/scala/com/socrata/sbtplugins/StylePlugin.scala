package com.socrata.sbtplugins

import java.io.{FileOutputStream, File}
import java.net.URL

import sbt._
import sbt.Keys._
import org.scalastyle.sbt.{ScalastylePlugin, Tasks => ScalastyleTasks}

import scala.language.implicitConversions

/**
 *Wraps scalastyle-sbt-plugin.
 *
 * Configuration files for main and test are included as resources.
 * See also [[http://www.scalastyle.org/]]
 */
object StylePlugin extends AutoPlugin {
  /** When to enable this autoplugin.
    * @return On all requirements in all scopes. */
  override def trigger: PluginTrigger = allRequirements
  /** Depends on these autoplugins.
    * @return Basic jvm. */
  override def requires: Plugins = plugins.JvmPlugin

  /** Settings for the project scope.
    * @return Settings to import in the project scope. */
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

  private[this] def configSettings: Seq[Setting[_]] = Seq(
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

  /** Exposed tasks and settings */
  object StyleKeys {
    /** Check scala source files using scalastyle. */
    val styleCheck = TaskKey[Unit]("styleCheck", "Check scala source files using scalastyle")
    /** Location of scalastyle config file. */
    val styleConfigName = SettingKey[String]("styleConfigName", "scalastyle config file")
    /** Location of scalastyle result file. */
    val styleResultName = SettingKey[String]("styleResultName", "scalastyle result file")
  }

  private[this] def getFileFromJar(state: State, url: URL, target: File): File = {
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
        case c: Any => state.log.error("unknown connection type %s".format(c.toString))
      }
    } catch {
      case e: java.io.IOException => state.log.error(e.getMessage)
    }

    target
  }
}
