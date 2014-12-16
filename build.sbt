name := "socrata-sbt-plugins"

organization := "com.socrata"

scalaVersion in Global := "2.10.4"

sbtPlugin := true

resolvers ++= Seq(
  "socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release",
  "socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  "sonatype release"  at "https://oss.sonatype.org/content/repositories/releases/",
  "sonatype snapshot" at "https://oss.sonatype.org/content/repositories/snapshots/",
  Resolver.mavenLocal,
  Classpaths.sbtPluginReleases
)

addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.2-SNAPSHOT")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.5.1")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.1-SNAPSHOT")

(scalastyleConfig in Compile) := baseDirectory.value / "resources/scalastyle-config.xml"

(scalastyleConfig in Test) := baseDirectory.value / "resources/scalastyle-test-config.xml"
