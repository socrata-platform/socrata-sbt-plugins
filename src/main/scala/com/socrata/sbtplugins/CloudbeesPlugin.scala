package com.socrata.sbtplugins

import sbt.Keys._
import sbt.Resolver.{ivyStylePatterns => ivy}
import sbt._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import sbtrelease.{Vcs, Git, ReleaseStep}

import scala.language.postfixOps

object CloudbeesPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = plugins.JvmPlugin && MimaPlugin && ReleasePlugin && WebDavPlugin

  override def projectSettings: Seq[Def.Setting[_]] = Seq(
    organization := "com.socrata",
    resolvers ++= Seq(Classpaths.sbtPluginReleases, Resolver.mavenLocal, SocrataMavenRelease, SocrataIvyRelease),
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

  // $COVERAGE-OFF$ covered by scripted sbt test
  lazy val approveChangelog: ReleaseStep = { st: State =>
    val git = gitOrHalt(getVcs(st))
    val lastRelease = lastReleaseTag(git)
    val gitlog = gitLog(git, lastRelease)

    val changeLogText = changeLog(lastRelease, gitlog, st.log)
    continueRelease(changeLogText, SimpleReader, st.log)
    st
  }

  def getVcs(st: State): Option[Vcs] = Project.extract(st).get(versionControlSystem)
  // $COVERAGE-ON$

  // Currently only supporting git repositories; however, sbt-release also supports Mercurial
  def gitOrHalt(vcs: Option[Vcs]): Git = vcs match {
    case Some(g: Git) => g
    case _ => sys.error("Current directory is not a git repository; aborting release...")
  }

  def lastReleaseTag(git: Git): String = {
    val tagPattern = "v[0-9]*"
    // 'git describe' only works if at least one tag is present
    if ((git.cmd("tag", "--list", tagPattern) !!).trim.length == 0) {
      // Gets first commit
      (git.cmd("rev-list", "--max-parents=0", "HEAD") !!).trim
    } else {
      (git.cmd("describe", "--tags", "--abbrev=0", "--match=%s".format(tagPattern)) !!).trim
    }
  }

  // Formats commit entries as 'hash author date subject'
  def gitLog(git: Git, lastRelease: String): String = (git.cmd("log", "%s..HEAD".format(lastRelease),
    "--pretty=format:\"%h %<(20)%an %ci %s\"") !!).trim.replaceAll("\"","")

  def changeLog(lastRelease: String, gitlog: String, logger: Logger): String = {
    val s = "\n** Changes since release %s: **\n%s\n ".format(lastRelease, gitlog)
    logger.info(s)
    s
  }

  val promptMsg = "Are you aware of all these changes and are they ready to be released? (y/n): "
  @annotation.tailrec
  def continueRelease(changeLogText: String, reader: LineReader, logger: Logger): Boolean =
    reader.readLine(promptMsg).map(_.toLowerCase) match {
      case Some("y") =>
        scala.tools.nsc.io.File("target/release-manifest").writeAll(changeLogText)
        true
      case Some("n") => sys.error("Please verify any unknown changes before releasing. Aborting...")
      case _ => continueRelease(changeLogText, reader, logger)
    }
}
