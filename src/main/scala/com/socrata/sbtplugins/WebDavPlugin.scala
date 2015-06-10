package com.socrata.sbtplugins

// Upstream:
// https://bitbucket.org/diversit/webdav4sbt
//
// From the original POM file:
// <license>
// <name>Eclipse Public License v1.0</name>
// <url>http://www.eclipse.org/legal/epl-v10.html</url>
// <distribution>repo</distribution>
// </license>

import com.googlecode.sardine.util.SardineException
import com.googlecode.sardine.{Sardine, SardineFactory}
import com.socrata.sbtplugins.StringPath._
import com.typesafe.tools.mima.plugin.MimaKeys
import sbt.Keys._
import sbt._
import sbt.std.TaskStreams

import scala.util.{Failure, Success, Try}

object WebDavPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
      WebDavKeys.mkcol <<= (
        organization, name, version, crossScalaVersions, sbtVersion,
        crossPaths, publishTo, credentials, streams, publishMavenStyle, sbtPlugin) map goMkcol,
      publish <<= publish dependsOn (WebDavKeys.mkcol, MimaKeys.reportBinaryIssues),
      publishLocal <<= publishLocal dependsOn MimaKeys.reportBinaryIssues
  )

  // $COVERAGE-OFF$ covered by scripted sbt test
  object WebDavKeys {
    lazy val webdav = config("webdav")
    lazy val mkcol = TaskKey[Unit]("mkcol", "Make collection (folder) in remote WebDav location.")
  }

  def goMkcol(organization: String, artifactName: String, version: String, // scalastyle:ignore parameter.number
              crossScalaVersions: Seq[String], sbtVersion: String, crossPaths: Boolean,
              publishTo: Option[Resolver], credentialsSet: Seq[Credentials], streams: TaskStreams[_],
              mavenStyle: Boolean, sbtPlugin: Boolean): Unit = {
    mkcolAction(organization, artifactName, version, crossScalaVersions, sbtVersion, crossPaths,
      publishTo, credentialsSet, SardineFactory.begin, streams.log, mavenStyle, sbtPlugin)
  }
  // $COVERAGE-ON$

  /**
   * Create artifact pathParts
   * -when is sbtPlugin then sbt version must be added to path
   * -when not crossPaths then not add any version number to path
   * -otherwise add scala version to path
   *
   * -when Scala 2.10.x then only add 2.10 to path
   * -otherwise add whole version to path (e.g. 2.9.2)
   */
  def createPaths(organization: String, artifactName: String, version: String, crossScalaVersions: Seq[String],
                  sbtVersion: String, crossPaths: Boolean, mavenStyle: Boolean, isSbtPlugin: Boolean): Seq[String] = {
    if (crossPaths) {
      crossScalaVersions map { scalaV =>
        def topLevel(v: String, level: Int): String = v split '.' take level mkString "."
        // The publish location for Scala 2.10.x is only '2.10', for Scala 2.9.x it is '2.9.x' !
        val scalaPubV = if (scalaV startsWith "2.10") topLevel(scalaV, 2) else scalaV

        if (isSbtPlugin) {
          // e.g. /com/organization/artifact_2.9.2_0.12/0.1
          organization.asPath / ("%s_%s_%s" format (artifactName, scalaPubV, topLevel(sbtVersion,2))) / version
        } else {
          organization.asPath / ("%s_%s" format (artifactName, scalaPubV)) / version
        }
      }
    } else {
      // e.g. /com/organization/artifact/0.1
      Seq(organization.asPath / artifactName / version)
    }
  }

  /**
   * Return all collections (folder) for path.
   * @param path "/part/of/url"
   * @return List("part","part/of","part/of/url")
   */
  def pathCollections(path: String): List[String] = {
    def pathParts(path: String): Seq[String] = path.substring(1) split "/" toSeq
    def addPathToUrls(urls: List[String], path: String): List[String] =
      if (urls.isEmpty) List(path) else urls :+ urls.last / path
    pathParts(path).foldLeft(List.empty[String])(addPathToUrls)
  }

  /**
   * Get Maven root from Resolver. Returns None if Resolver is not MavenRepository.
   */
  def mavenRoot(resolver: Option[Resolver]): Option[String] = resolver match {
    case Some(m: MavenRepository) => Some(m.root)
    case _ => None
  }

  def publishToUrls(paths: Seq[String], resolver: Option[Resolver]): Option[Seq[String]] = resolver match {
    case Some(m: MavenRepository) => Some(paths map { p => m.root / p })
    case _ => None
  }

  /**
   * Make collector (folder) for all paths.
   * @throws MkColException when there's any problem accessing/creating a publish path.
   */
  def mkcol(sardine: Sardine, urlRoot: String, paths: List[String], logger: Logger): Unit = {
    val notExistMsg = "Root '%s' does not exist."
    val errorMsg = "Could not access '%s'."
    Try { sardine.exists(urlRoot) } match {
      case Failure(e: Exception) => throw new MkColException(errorMsg format urlRoot, e)
      case Success(b: Boolean) if !b => throw new MkColException(notExistMsg format urlRoot)
      case _ =>
        paths foreach { p =>
          val fullUrl = urlRoot / p
          Try { sardine.exists(fullUrl) } match {
            case Failure(e: Exception) => throw new MkColException(errorMsg format fullUrl, e)
            case Success(b: Boolean) if !b =>
              logger.info("WebDav: Creating collection '%s'" format fullUrl)
              sardine.createDirectory(fullUrl)
            case _ => logger.info("WebDav: Found collection '%s'" format fullUrl)
          }
        }
    }
  }

  val hostRegex = """^http[s]?://([a-zA-Z0-9\.\-]*)[/]?.*$""".r
  def getCredentialsForHostOrElse(publishTo: Option[Resolver], creds: Seq[Credentials],
                            logger: Logger): DirectCredentials = {
    mavenRoot(publishTo) flatMap { r =>
      val hostRegex(host) = r
      logger.info("WebDav: Seeks credentials for host: %s" format host)
      Credentials.allDirect(creds) find {
        case c: DirectCredentials =>
          logger.info("WebDav: Found credentials for host: %s" format c.host)
          c.host == host
      }
    } match {
      case Some(creds: DirectCredentials) => creds
      case _ => throw new MkColException("WebDav: No credentials available to publish.")
    }
  }

  def makeCollections(publishTo: Option[Resolver], artifactPathParts: Seq[List[String]], creds: DirectCredentials,
                      sardineFactory: (String, String) => Sardine, logger: Logger): Unit = {
    mavenRoot(publishTo) foreach { r =>
      val sardine = sardineFactory(creds.userName, creds.passwd)
      artifactPathParts foreach { pp =>
        mkcol(sardine, r, pp, logger)
      }
    }
  }

  /**
   * Creates a collection for all artifacts that are going to be published
   * if the collection does not exist yet.
   */
  def mkcolAction(organization: String, artifactName: String, version: String, // scalastyle:ignore parameter.number
                  crossScalaVersions: Seq[String], sbtVersion: String, crossPaths: Boolean,
                  publishTo: Option[Resolver], credentialsSet: Seq[Credentials],
                  sardineFactory: (String, String) => Sardine, logger: Logger,
                  mavenStyle: Boolean, sbtPlugin: Boolean): Unit = {
    logger.info("WebDav: Check whether (new) collection needs to be created.")
    val artifactPaths = createPaths(
      organization, artifactName, version, crossScalaVersions, sbtVersion, crossPaths, mavenStyle, sbtPlugin)
    val artifactPathParts = artifactPaths map pathCollections
    val cc = getCredentialsForHostOrElse(publishTo, credentialsSet, logger)
    makeCollections(publishTo, artifactPathParts, cc, (u, p) => sardineFactory(u, p), logger)
    logger.info("WebDav: Done.")
  }

  case class MkColException(msg: String, inner: Throwable = null) extends // scalastyle:ignore null
    RuntimeException(msg, inner)
}
