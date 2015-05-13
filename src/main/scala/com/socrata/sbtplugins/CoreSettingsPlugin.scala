package com.socrata.sbtplugins

import com.socrata.sbtplugins.CoveragePlugin.CoverageKeys.coverageDisable
import com.socrata.sbtplugins.StylePlugin.StyleKeys.styleCheck
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys.assembly

/** Enables autoplugins, sets scala version and compiler flags for static analysis. */
object CoreSettingsPlugin extends AutoPlugin {
  /** When to enable this autoplugin.
    * @return On all requirements in all scopes. */
  override def trigger: PluginTrigger = allRequirements
  /** Depends on these autoplugins.
    * @return Basic jvm, others enabled by trigger. */
  override def requires: Plugins = plugins.JvmPlugin

  private val compileEncoding = Seq("-encoding", "UTF-8")
  private val compileDebug29 = Seq("-g:vars")
  private val compileDebug28 = Seq("-g")
  private val compileExplicitFeature = Seq("-feature")

  /** Settings for the project scope.
    * @return Settings to import in the project scope. */
  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    test in assembly := {},
    assembly <<= assembly dependsOn (styleCheck in Compile, coverageDisable),
    scalaVersion := "2.10.5",
    scalacOptions ++= compileEncoding ++ Seq("-Xlint", "-deprecation", "-Xfatal-warnings", "-unchecked"),
    scalacOptions <++= scalaVersion map {
      case ScalaVersion.Is28() => compileDebug28
      case ScalaVersion.Is29() => compileDebug29
      case ScalaVersion.Is210() => compileDebug29 ++ compileExplicitFeature
      case ScalaVersion.Is211() => compileDebug29 ++ compileExplicitFeature
      case v: String => throw new UnsupportedVersionError("version '%s' isn't within range [2.8, 2.11]" format v)
    },
    javacOptions in compile ++= compileEncoding ++ compileDebug28 ++
      Seq("-Xlint:unchecked", "-Xlint:deprecation", "-Xmaxwarns", "999999"),
    logBuffered in Test <<= parallelExecution in Test, // buffer log output only if tests are being run in parallel
    SocrataSbtKeys.dependenciesSnippet := <xml:group/>,
    ivyXML <<= SocrataSbtKeys.dependenciesSnippet { snippet =>
      <dependencies>
        {snippet.toList}
        <conflict org="com.socrata" manager="latest-compatible"/>
        <conflict org="com.rojoma" manager="latest-compatible"/>
      </dependencies>
    },
    unmanagedSourceDirectories in Compile <+= (scalaVersion, scalaSource in Compile) {
      (sv, commonSource) => commonSource.getParentFile / dir4ScalaV(sv)
    },
    unmanagedSourceDirectories in Test <+= (scalaVersion, scalaSource in Test) {
      (sv, commonSource) => commonSource.getParentFile / dir4ScalaV(sv)
    }
  )

  object SocrataSbtKeys {
    val dependenciesSnippet = SettingKey[xml.NodeSeq]("socrata-dependencies-snippet")
  }

  object ScalaVersion {
    object Is28 { def unapply(s: String): Boolean = s startsWith "2.8." }
    object Is29 { def unapply(s: String): Boolean = s startsWith "2.9." }
    object Is210 { def unapply(s: String): Boolean = s startsWith "2.10." }
    object Is211 { def unapply(s: String): Boolean = s startsWith "2.11." }
  }

  def dir4ScalaV(scalaVersion: String): String = {
    val MajorMinor = """(\d+\.\d+)\..*""".r
    scalaVersion match {
      case MajorMinor(mm) => "scala-%s" format mm
      case _ => throw new UnsupportedVersionError("Unable to find major/minor Scala version in %s" format scalaVersion)
    }
  }

  case class UnsupportedVersionError(msg: String) extends Exception(msg)
}
