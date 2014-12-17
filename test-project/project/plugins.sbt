// build root project
lazy val root = Project("plugins", file(".")).dependsOn(plugin)

lazy val plugin = ProjectRef(file("..").getCanonicalFile.toURI, "socrata-sbt-plugins")

dependencyOverrides += "org.scala-sbt" % "sbt" % "0.13.5"
