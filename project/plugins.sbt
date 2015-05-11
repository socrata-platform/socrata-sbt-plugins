// Use project source code in the build definition, (baseDir is ./project/ in this case)
unmanagedSourceDirectories in Compile +=
  baseDirectory.value / "../src/main/scala/"

// If you update this list of dependencies, remember to update ../build.sbt too
libraryDependencies += ("org.scala-sbt" % "scripted-plugin" % sbtVersion.value).
  exclude("org.scala-sbt", "precompiled-2_8_2").
  exclude("org.scala-sbt", "precompiled-2_9_2").
  exclude("org.scala-sbt", "precompiled-2_9_3")
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "com.googlecode.sardine" % "sardine" % "146"
libraryDependencies += "joda-time" % "joda-time" % "2.7"
libraryDependencies += "org.joda" % "joda-convert" % "1.7"
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.0.4")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.6.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.5")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.6")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.4.0")
