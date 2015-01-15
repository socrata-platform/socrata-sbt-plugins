resolvers ++= Seq(Classpaths.sbtPluginReleases, Resolver.mavenLocal,
  Resolver.url("thricejamie bintray", url("http://dl.bintray.com/thricejamie/sbt-plugins"))(Resolver.ivyStylePatterns),
  //  "sonatype snapshot" at "https://oss.sonatype.org/content/repositories/snapshots",
  "sonatype release"  at "https://oss.sonatype.org/content/repositories/releases",
  //  "socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  "socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release"
)

// If you update this list of dependencies, remember to update ../build.sbt too
libraryDependencies <+= sbtVersion { "org.scala-sbt" % "scripted-plugin" % _ }
//TODO: fix socrata cloudbees sbt plugin interference with tasks
//addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.2")
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.1")
addSbtPlugin("com.37pieces" % "sbt-meow" % "0.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")
