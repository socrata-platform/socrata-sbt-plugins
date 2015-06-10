package com.socrata.sbtplugins

import org.joda.time.{DateTime, DateTimeZone}
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtbuildinfo.{BuildInfoPlugin => OriginalPlugin}
import scoverage.ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages

object BuildInfoPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin && OriginalPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    coverageExcludedPackages := "%s.BuildInfo;%s".format(buildInfoPackage.value, coverageExcludedPackages.value),
    buildInfoKeys ++= Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      sbtVersion,
      BuildInfoKey.action("buildTime") { DateTime.now(DateTimeZone.UTC).toDateTimeISO },
      BuildInfoKey.action("revision") { gitRevision }
    ),
    buildInfoOptions ++= Seq(BuildInfoOption.ToMap, BuildInfoOption.ToJson)
  )

  private[this] class SimpleProcessLog extends ProcessLogger {
    var errorString: Option[String] = None
    var infoString: Option[String] = None
    override def buffer[T](f: => T): T = f
    override def error(s: => String): Unit = errorString = Some(s)
    override def info(s: => String): Unit = infoString = Some(s)
  }

  def gitRevision: String = {
    val procLog = new SimpleProcessLog
    val exitCode = Process(Seq("git", "describe", "--always", "--dirty", "--long", "--abbrev=40")).!(procLog)
    exitCode match {
      case 0 => procLog.infoString.getOrElse("git stdout = null")
      case n: Int => s"git error $n: ${procLog.errorString.getOrElse("git stderr = null")}"
    }
  }
}
