resolvers ++= Seq(
  "socrata release" at "https://repository-socrata-oss.forge.cloudbees.com/release",
  "socrata snapshot" at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  Classpaths.sbtPluginReleases
)

addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.2-SNAPSHOT")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.5.1")
