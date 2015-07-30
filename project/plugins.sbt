// Use project source code in the build definition, (baseDir is ./project/ in this case)
unmanagedSourceDirectories in Compile +=
  baseDirectory.value / "../src/main/scala/"

// If you update this list of dependencies, remember to update ../build.sbt too
libraryDependencies += ("org.scala-sbt" % "scripted-plugin" % sbtVersion.value).
  exclude("org.scala-sbt", "precompiled-2_8_2").
  exclude("org.scala-sbt", "precompiled-2_9_2").
  exclude("org.scala-sbt", "precompiled-2_9_3")
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += "com.google.code.findbugs" % "findbugs" % "3.0.0"
libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.0"
libraryDependencies += "com.googlecode.sardine" % "sardine" % "146"
libraryDependencies += "joda-time" % "joda-time" % "2.7"
libraryDependencies += "org.joda" % "joda-convert" % "1.7"
libraryDependencies += "commons-io" % "commons-io" % "2.4"
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "1.2.0")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.5")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.13.0")
addSbtPlugin("com.eed3si9n" % "sbt-sequential" % "0.1.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.1")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.6")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.4.0")
addSbtPlugin("de.johoop" % "findbugs4sbt" % "1.4.0")
