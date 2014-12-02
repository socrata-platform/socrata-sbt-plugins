name := "socrata-sbt-plugins"

organization := "com.socrata"

scalaVersion in Global := "2.10.4"

sbtPlugin := true

addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.2-SNAPSHOT")

addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.5.1")
