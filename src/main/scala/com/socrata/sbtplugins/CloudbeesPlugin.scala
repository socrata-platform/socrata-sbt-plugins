package com.socrata.sbtplugins

import sbt._
import sbt.Keys._
import sbt.Resolver.{ivyStylePatterns => ivy}
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.{Git, ReleaseStep}

import scala.language.postfixOps

object CloudbeesPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin && MimaPlugin && ReleasePlugin && WebDavPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    organization := "com.socrata",
    resolvers ++= Seq(SocrataMavenRelease, SocrataIvyRelease),
    resolvers <++= isSnapshot { if (_) Seq(SocrataMavenSnapshot, SocrataIvySnapshot) else Nil },
    publishTo <<= isSnapshot { if (_) Some(SocrataMavenSnapshot) else Some(SocrataMavenRelease) },
    pomIncludeRepository := { _ => false },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    credentials <++= streams map { s =>
      List(new File("/private/socrata-oss/maven-credentials")).flatMap { f =>
        if (f.exists) {
          s.log.info("Loading credentials from %s" format f)
          Some(Credentials(f))
        } else {
          s.log.info("Cloudbees credentials file not found")
          None
        }
      }
    },
    releaseProcess := cloudbeesReleaseSteps
    )

  val SocrataRepoBase = "https://repository-socrata-oss.forge.cloudbees.com/"
  val SocrataMavenRelease = "socrata maven releases" at (SocrataRepoBase + "release")
  val SocrataMavenSnapshot = "socrata maven snapshots" at (SocrataRepoBase + "snapshot")
  val SocrataIvyRelease = Resolver.url("socrata ivy releases", new URL(SocrataRepoBase + "ivy-release"))(ivy)
  val SocrataIvySnapshot = Resolver.url("socrata ivy snapshots", new URL(SocrataRepoBase + "ivy-snapshot"))(ivy)

  object SocrataSbtKeys {
    val dependenciesSnippet = SettingKey[xml.NodeSeq]("socrata-dependencies-snippet")
  }

  object SocrataUtil {
    object Is28 { def unapply(s: String): Boolean = s startsWith "2.8." }
    object Is29 { def unapply(s: String): Boolean = s startsWith "2.9." }
    object Is210 { def unapply(s: String): Boolean = s startsWith "2.10." }
  }

  val cloudbeesReleaseSteps: Seq[ReleaseStep] = Seq(
    checkSnapshotDependencies,
    runClean,
    runTest,
    approveChangelog,
    inquireVersions,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )

  lazy val approveChangelog: ReleaseStep = { st: State =>
    val tagPattern = "v[0-9]*"
    // Currently only supporting git repositories; however, sbt-release also supports Mercurial
    val git = Project.extract(st).get(versionControlSystem) match {
      case Some(vcs: Git) =>
        vcs
      case Some(_: Any) | None =>
        sys.error("Current directory is not a git repository; aborting release...")
    }
    val lastRelease =
    // 'git describe' only works if at least one tag is present
      if ((git.cmd("tag", "--list", tagPattern) !!).trim.length == 0) {
        // Gets first commit
        (git.cmd("rev-list", "--max-parents=0", "HEAD") !!).trim
      } else {
        (git.cmd("describe", "--tags", "--abbrev=0", "--match=%s".format(tagPattern)) !!).trim
      }

    // Formats commit entries as 'hash author date subject'
    val gitlog = (git.cmd("log", "%s..HEAD".format(lastRelease),
      "--pretty=format:\"%h %<(20)%an %ci %s\"") !!).trim.replaceAll("\"","")
    val changeLogText = "\n** Changes since release %s: **\n%s\n ".format(lastRelease, gitlog)
    st.log.info(changeLogText)

    val promptMsg = "Are you aware of all these changes and are they ready to be released? (y/n): "
    @annotation.tailrec
    def continueRelease(state: State): State = SimpleReader.readLine(promptMsg).map(_.toLowerCase) match {
      case Some("y") =>
        scala.tools.nsc.io.File("target/release-manifest").writeAll(changeLogText)
        state
      case Some("n") =>
        sys.error("Please verify any unknown changes before releasing. Aborting...")
      case Some(_) | None =>
        st.log.warn("Unrecognized input; options are (y/n)")
        continueRelease(state)
    }
    continueRelease(st)
  }
}
