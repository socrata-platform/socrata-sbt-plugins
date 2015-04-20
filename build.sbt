name := "socrata-sbt-plugins"
organization := "com.socrata"
scalaVersion in Global := "2.10.4"
sbtPlugin := true

// if you update this list of repos remember to update project/plugins.sbt too.
libraryDependencies += ("org.scala-sbt" % "scripted-plugin" % sbtVersion.value).
  exclude("org.scala-sbt", "precompiled-2_8_2").
  exclude("org.scala-sbt", "precompiled-2_9_2").
  exclude("org.scala-sbt", "precompiled-2_9_3")
libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.0" % "test"
libraryDependencies += "com.googlecode.sardine" % "sardine" % "146"
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.6")

(StylePlugin.StyleKeys.styleConfigName in Compile) := None
(StylePlugin.StyleKeys.styleConfigName in Test) := None
(scalastyleConfig in Compile) := baseDirectory.value / "src/main/resources/scalastyle-config.xml"
(scalastyleConfig in Test) := baseDirectory.value / "src/main/resources/scalastyle-test-config.xml"

// Scripted - sbt plugin tests
scriptedSettings
scriptedLaunchOpts <+= version apply { v => "-Dproject.version="+v }
scriptedLaunchOpts += "-XX:MaxPermSize=256M"
scriptedBufferLog := false

scalacOptions ++= Seq("-language:postfixOps", "-language:implicitConversions")

// See: https://github.com/sbt/sbt-assembly/blob/master/README.md#merge-strategy
assemblyMergeStrategy in assembly := {
  case "sbt/sbt.autoplugins" => MergeStrategy.concat
  case "sbt/sbt.plugins" => MergeStrategy.concat
  case "scalastyle-config.xml" => MergeStrategy.first
  case otherPath =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(otherPath)
}

sbtrelease.ReleasePlugin.ReleaseKeys.releaseProcess := CloudbeesPlugin.cloudbeesReleaseSteps
