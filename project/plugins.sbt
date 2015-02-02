import Resolver.{ivyStylePatterns => ivy}
resolvers ++= Seq(Classpaths.sbtPluginReleases, Resolver.mavenLocal,
  Resolver.url("thricejamie bintray", url("http://dl.bintray.com/thricejamie/sbt-plugins"))(ivy),
  Resolver.url("sbt-plugin-releases", url("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(ivy),
  //  "sonatype snapshot" at "https://oss.sonatype.org/content/repositories/snapshots",
  "sonatype release"  at "https://oss.sonatype.org/content/repositories/releases",
  //  "socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  "socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release"
)

// If you update this list of dependencies, remember to update ../build.sbt too
libraryDependencies += ("org.scala-sbt" % "scripted-plugin" % sbtVersion.value).
  exclude("org.scala-sbt", "precompiled-2_8_2").
  exclude("org.scala-sbt", "precompiled-2_9_2").
  exclude("org.scala-sbt", "precompiled-2_9_3")
//TODO: fix socrata cloudbees sbt plugin interference with tasks
//addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.2")
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.1")
addSbtPlugin("com.37pieces" % "sbt-meow" % "0.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")
