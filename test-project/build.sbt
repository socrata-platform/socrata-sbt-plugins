name := "test-project"

version := "1.0"

addSbtPlugin("com.socrata" % "socrata-sbt-plugins" % "0.0.1-SNAPSHOT")

resolvers ++= Seq(
  "socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  "sonatype snapshot" at "https://oss.sonatype.org/content/repositories/snapshots/",
  Resolver.mavenLocal
)
