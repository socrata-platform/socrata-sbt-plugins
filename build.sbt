name := "socrata-sbt-plugins"
organization := "com.socrata"
scalaVersion in Global := "2.10.4"
sbtPlugin := true

resolvers ++= Seq(
  "socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release",
//  "socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot",
  "sonatype release"  at "https://oss.sonatype.org/content/repositories/releases",
//  "sonatype snapshot" at "https://oss.sonatype.org/content/repositories/snapshots",
  "thricejamie bintray" at "http://dl.bintray.com/thricemamie/sbt-plugins",
  Resolver.mavenLocal,
  Classpaths.sbtPluginReleases
)

addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.2")
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.1")
addSbtPlugin("com.37pieces" % "sbt-meow" % "0.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")

(scalastyleConfig in Compile) := baseDirectory.value / "src/main/resources/scalastyle-config.xml"
(scalastyleConfig in Test) := baseDirectory.value / "src/main/resources/scalastyle-test-config.xml"

lazy val testStyleTask = taskKey[Unit]("a task that wraps 'test:scalastyle' with no input parameters.")
testStyleTask := { val _ = (scalastyle in Test).toTask("").value }
(test in Test) <<= (test in Test) dependsOn (testStyleTask in Test)

lazy val mainStyleTask = taskKey[Unit]("a task that wraps 'scalastyle' with no input parameters.")
mainStyleTask := { val _ = (scalastyle in Compile).toTask("").value }
(Keys.`package` in Compile) <<= (Keys.`package` in Compile) dependsOn (mainStyleTask in Compile)
