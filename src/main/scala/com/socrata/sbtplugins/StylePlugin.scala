package com.socrata.sbtplugins

import org.scalastyle.sbt.ScalastylePlugin._
import org.scalastyle.sbt.{ScalastylePlugin => OriginalPlugin, Tasks => OriginalTasks}
import sbt.Keys._
import sbt._

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
    OriginalPlugin.projectSettings ++
    inConfig(Compile)(configSettings) ++
    inConfig(Test)(configSettings) ++ Seq(
      (StyleKeys.styleConfigName in Compile) := Some("/scalastyle-config.xml"),
      (StyleKeys.styleConfigName in Test) := Some("/scalastyle-test-config.xml"),
      (StyleKeys.styleResultName in Compile) := "/scalastyle-result.xml",
      (StyleKeys.styleResultName in Test) := "/scalastyle-test-result.xml",
      (StyleKeys.styleFailOnError in Compile) := true,
      (StyleKeys.styleFailOnError in Test) := true,
      (test in Test) <<= (test in Test) dependsOn (StyleKeys.styleCheck in Test),
      (Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn (StyleKeys.styleCheck in Compile)
    )

  private[this] def configSettings: Seq[Setting[_]] = Seq(
    StyleKeys.styleCheck := {
      val args = Seq()
      val configXml = StyleKeys.styleConfigName.value match {
        case Some(f) => new JArchive(getClass.getResource(f)).getFileFromJar(target.value / f, state.value.log)
        case None => scalastyleConfig.value
      }
      val configUrl = None
      val failOnError = StyleKeys.styleFailOnError.value
      val sourceDir = (scalaSource in StyleKeys.styleCheck).value
      val outputXml = target.value / StyleKeys.styleResultName.value
      val localStreams = streams.value
      val configRefreshHours = 0
      val configUrlCacheFilePath = "/dev/null"

      state.value.log.info(s"Starting scalastyle with failOnError=$failOnError")
      OriginalTasks.doScalastyle(
        args,
        configXml,
        configUrl,
        failOnError,
        sourceDir,
        outputXml,
        localStreams,
        configRefreshHours,
        target.value,
        configUrlCacheFilePath
      )
    }
  )

  /** Exposed tasks and settings */
  object StyleKeys {
    /** Check scala source files using scalastyle. */
    val styleCheck = TaskKey[Unit]("styleCheck", "Check scala source files using scalastyle")
    /** Location of scalastyle config file. */
    val styleConfigName = SettingKey[Option[String]]("styleConfigName", "scalastyle config file")
    /** Location of scalastyle result file. */
    val styleResultName = SettingKey[String]("styleResultName", "scalastyle result file")
    /** Fail the build on error. */
    val styleFailOnError = SettingKey[Boolean]("styleFailOnError", "scalastyle errors cause build to fail",
      KeyRanks.ASetting)
  }
}
