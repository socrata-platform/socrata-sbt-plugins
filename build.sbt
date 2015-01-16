name := "socrata-sbt-plugins"
organization := "com.socrata"
scalaVersion in Global := "2.10.4"
sbtPlugin := true

resolvers ++= Seq(Classpaths.sbtPluginReleases, Resolver.mavenLocal,
  Resolver.url("thricejamie bintray", url("http://dl.bintray.com/thricejamie/sbt-plugins"))(Resolver.ivyStylePatterns),
  //  "sonatype snapshot" at "https://oss.sonatype.org/content/repositories/snapshots",
  "sonatype release"  at "https://oss.sonatype.org/content/repositories/releases",
  //  "socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  "socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release"
)

publishTo <<= isSnapshot {s =>
  if (s) {Some("socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot")}
  else {Some("socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release")}
}
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

// if you update this list of repos remember to update project/plugins.sbt too.
libraryDependencies <+= sbtVersion { "org.scala-sbt" % "scripted-plugin" % _ }
//TODO: fix socrata cloudbees sbt plugin interference with tasks
//addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.2")
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.1")
addSbtPlugin("com.37pieces" % "sbt-meow" % "0.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")

(scalastyleConfig in Compile) := baseDirectory.value / "src/main/resources/scalastyle-config.xml"
(scalastyleConfig in Test) := baseDirectory.value / "src/main/resources/scalastyle-test-config.xml"
lazy val testStyleTask = taskKey[Unit]("a task that wraps 'test:scalastyle' with no input parameters.")
testStyleTask := { val _ = (scalastyle in Test).toTask("").value }
(test in Test) <<= (test in Test) dependsOn (testStyleTask in Test)
lazy val mainStyleTask = taskKey[Unit]("a task that wraps 'scalastyle' with no input parameters.")
mainStyleTask := { val _ = (scalastyle in Compile).toTask("").value }
(Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn (mainStyleTask in Compile)

import ScoverageSbtPlugin.ScoverageKeys
ScoverageKeys.coverageHighlighting := false
ScoverageKeys.coverageMinimum := 100
ScoverageKeys.coverageFailOnMinimum := false
lazy val coverageIsEnabled = taskKey[Unit]("tells whether sbt-coverage is enabled")
coverageIsEnabled := { state.value.log.info("scoverage enabled: %s".format(ScoverageSbtPlugin.enabled)) }
lazy val coverageDisable = taskKey[Unit]("disables sbt-coverage plugin.")
coverageDisable := { ScoverageSbtPlugin.enabled = false }
(Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn coverageDisable

// Scripted - sbt plugin tests
scriptedSettings
scriptedLaunchOpts <+= version apply { v => "-Dproject.version="+v }
scriptedBufferLog := false
