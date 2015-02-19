name := "socrata-sbt-plugins"
organization := "com.socrata"
scalaVersion in Global := "2.10.4"
sbtPlugin := true

import Resolver.{ivyStylePatterns => ivy}
resolvers ++= Seq(Classpaths.sbtPluginReleases, Resolver.mavenLocal,
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
libraryDependencies += ("org.scala-sbt" % "scripted-plugin" % sbtVersion.value).
  exclude("org.scala-sbt", "precompiled-2_8_2").
  exclude("org.scala-sbt", "precompiled-2_9_2").
  exclude("org.scala-sbt", "precompiled-2_9_3")
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.0" % "test"
libraryDependencies += "com.googlecode.sardine" % "sardine" % "146"
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.6")

net.virtualvoid.sbt.graph.Plugin.graphSettings

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

assembly in Compile <<= assembly in Compile dependsOn (mainStyleTask in Compile, coverageDisable)
test in assembly := {}
scalacOptions ++= Seq("-language:postfixOps", "-language:implicitConversions")
pomIncludeRepository := Classpaths.defaultRepositoryFilter
// See: https://github.com/sbt/sbt-assembly/blob/master/README.md#merge-strategy
assemblyMergeStrategy in assembly := {
  case "sbt/sbt.autoplugins" => MergeStrategy.concat
  case "sbt/sbt.plugins" => MergeStrategy.concat
  case "scalastyle-config.xml" => MergeStrategy.first
  case otherPath =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(otherPath)
}

releaseSettings

mimaDefaultSettings
