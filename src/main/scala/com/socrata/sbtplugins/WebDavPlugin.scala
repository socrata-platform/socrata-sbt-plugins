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

import sbt._
import sbt.Keys._
import sbt.std.TaskStreams

import com.socrata.sbtplugins.StringPath._
import com.typesafe.tools.mima.plugin.MimaKeys
import com.googlecode.sardine.{SardineFactory, Sardine}

object WebDavPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin

  override def projectSettings: Seq[Def.Setting[_]] = super.projectSettings

  trait WebDavKeys {
    lazy val webdav = config("webdav")
    lazy val mkcol = TaskKey[Unit]("mkcol", "Make collection (folder) in remote WebDav location.")
  }

  trait MkCol {

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
     * Check if url exists.
     */
    def exists(sardine: Sardine, url: String): Either[Throwable,Boolean] = {
      try {
        Right(sardine.exists(url))
      } catch {
        case e: Throwable => Left(e)
      }
    }

    /**
     * Make collector (folder) for all paths.
     * @throws MkColException when there's any problem accessing/creating a publish path.
     */
    def mkcol(sardine: Sardine, urlRoot: String, paths: List[String], logger: Logger): Unit = {
      val notExistMsg = "Root '%s' does not exist."
      val errorMsg = "Could not access '%s'."
      exists(sardine, urlRoot) match {
        case Left(e) => throw new MkColException(errorMsg format urlRoot, e)
        case Right(b) if !b => throw new MkColException(notExistMsg format urlRoot)
        case _ =>
          paths foreach { p =>
            val fullUrl = urlRoot / p
            exists(sardine, fullUrl) match {
              case Left(e) => throw new MkColException(errorMsg format fullUrl, e)
              case Right(b) if !b =>
                logger.info("WebDav: Creating collection '%s'" format fullUrl)
                sardine.createDirectory(fullUrl)
              case _ => logger.info("WebDav: Found collection '%s'" format fullUrl)
            }
          }
      }
    }

    val hostRegex = """^http[s]?://([a-zA-Z0-9\.\-]*)/.*$""".r
    def getCredentialsForHost(publishTo: Option[Resolver], creds: Seq[Credentials],
                              streams: TaskStreams[_]): Option[DirectCredentials] = {
      mavenRoot(publishTo) flatMap { r =>
        val hostRegex(host) = r
        Credentials.allDirect(creds) find {
          case c: DirectCredentials =>
            streams.log.info("WebDav: Found credentials for host: %s" format c.host)
            c.host == host
          case _ => false
        }
      }
    }

    /**
     * Creates a collection for all artifacts that are going to be published
     * if the collection does not exist yet.
     */
    def mkcolAction(organization: String, artifactName: String, version: String, // scalastyle:ignore parameter.number
                    crossScalaVersions: Seq[String], sbtVersion: String, crossPaths: Boolean,
                    publishTo: Option[Resolver], credentialsSet: Seq[Credentials], streams: TaskStreams[_],
                    mavenStyle: Boolean, sbtPlugin: Boolean): Unit = {
      streams.log.info("WebDav: Check whether (new) collection needs to be created.")
      val artifactPaths = createPaths(
        organization, artifactName, version, crossScalaVersions, sbtVersion, crossPaths, mavenStyle, sbtPlugin)
      val artifactPathParts = artifactPaths map pathCollections

      def makeCollections(credentials: DirectCredentials): Unit = {
        mavenRoot(publishTo) foreach { r =>
          val sardine = SardineFactory.begin(credentials.userName, credentials.passwd)
          artifactPathParts foreach { pp =>
            mkcol(sardine, r, pp, streams.log)
          }
        }
      }

      val cc = getCredentialsForHost(publishTo, credentialsSet, streams)
      cc match {
        case Some(creds: DirectCredentials) => makeCollections(creds)
        case _ => throw new MkColException("No credentials available to publish to WebDav.")
      }

      streams.log.info("WebDav: Done.")
    }

    case class MkColException(msg: String, inner: Throwable = null) extends // scalastyle:ignore null
      RuntimeException(msg, inner)
  }

  object WebDav extends MkCol with WebDavKeys {
    val globalSettings = Seq(
      mkcol <<= (
        organization, name, version, crossScalaVersions, sbtVersion,
        crossPaths, publishTo, credentials, streams, publishMavenStyle, sbtPlugin) map mkcolAction,
      publish <<= publish dependsOn (mkcol, MimaKeys.reportBinaryIssues),
      publishLocal <<= publishLocal dependsOn MimaKeys.reportBinaryIssues
    )

    val scopedSettings = inConfig(webdav)(globalSettings)
  }
}
