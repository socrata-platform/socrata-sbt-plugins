// build root project
lazy val root = Project("plugins", file(".")).dependsOn(plugin)

lazy val plugin = ProjectRef(file("..").getCanonicalFile.toURI, "socrata-sbt-plugins")

dependencyOverrides += "org.scala-sbt" % "sbt" % "0.13.5"

resolvers ++= Seq(
  "socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  "sonatype snapshot" at "https://oss.sonatype.org/content/repositories/snapshots/"
)
