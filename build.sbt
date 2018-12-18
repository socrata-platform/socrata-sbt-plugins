name := "socrata-sbt-plugins"
sbtPlugin := true

// if you update this list of repos remember to update project/plugins.sbt too.
libraryDependencies += ("org.scala-sbt" % "scripted-plugin" % sbtVersion.value).
  exclude("org.scala-sbt", "precompiled-2_8_2").
  exclude("org.scala-sbt", "precompiled-2_9_2").
  exclude("org.scala-sbt", "precompiled-2_9_3")
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "com.google.code.findbugs" % "findbugs" % "3.0.0"
libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.0"
libraryDependencies += "com.googlecode.sardine" % "sardine" % "146"
libraryDependencies += "joda-time" % "joda-time" % "2.7"
libraryDependencies += "org.joda" % "joda-convert" % "1.7"
libraryDependencies += "commons-io" % "commons-io" % "2.4"
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.2.0")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.8.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")
addSbtPlugin("com.eed3si9n" % "sbt-sequential" % "0.1.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.1")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.6")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.4.0")
addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.4.0")

(StylePlugin.StyleKeys.styleConfigName in Compile) := None
(StylePlugin.StyleKeys.styleConfigName in Test) := None
(scalastyleConfig in Compile) := baseDirectory.value / "src/main/resources/scalastyle-config.xml"
(scalastyleConfig in Test) := baseDirectory.value / "src/main/resources/scalastyle-test-config.xml"

// Scripted - sbt plugin tests
scriptedSettings
scriptedLaunchOpts <+= version apply { v => "-Dproject.version="+v }
scriptedLaunchOpts += "-XX:MaxPermSize=256M"
scriptedBufferLog := false
scripted <<= scripted dependsOn scoverage.ScoverageSbtPlugin.ScoverageKeys.coverageOff

scalacOptions ++= Seq("-language:postfixOps", "-language:implicitConversions")

// See: https://github.com/sbt/sbt-assembly/blob/master/README.md#merge-strategy
assemblyMergeStrategy in assembly := {
  case "sbt/sbt.autoplugins" => MergeStrategy.concat
  case "sbt/sbt.plugins" => MergeStrategy.concat
  case "scalastyle-config.xml" => MergeStrategy.first
  case "version.properties" => MergeStrategy.discard
  case otherPath =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(otherPath)
}

enablePlugins(sbtbuildinfo.BuildInfoPlugin)
buildInfoPackage := "com.socrata.sbtplugins"

publishTo := {
      val nexus = "https://repo.socrata.com/artifactory/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "libs-snapshot-local")
      else
        Some("releases"  at nexus + "libs-release-local")
}

publishMavenStyle := true

val artifactoryResolver = Resolver.url(
  "Artifactory Realm",
  url("https://repo.socrata.com"))(
  Patterns("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]") )

resolvers += artifactoryResolver

publishTo := Some(artifactoryResolver)
