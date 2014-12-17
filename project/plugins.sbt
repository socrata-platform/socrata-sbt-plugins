resolvers ++= Seq(
  "socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release",
  "socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  "sonatype release"  at "https://oss.sonatype.org/content/repositories/releases/",
  "sonatype snapshot" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "thricejamie bintray" at "http://dl.bintray.com/thricemamie/sbt-plugins",
  Resolver.mavenLocal,
  Classpaths.sbtPluginReleases
)

addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.2-SNAPSHOT")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.1")

addSbtPlugin("com.37pieces" % "sbt-meow" % "0.1")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.1-SNAPSHOT")
