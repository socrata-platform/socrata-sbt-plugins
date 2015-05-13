# socrata-sbt-plugins
A repository for plugins that can be used across projects.

## Usage
### Upgrading from socrata-cloudbees-sbt
Build-build scope, usually `project/plugins.sbt`

1. Remove any version(s) of `socrata-cloudbees-sbt` plugin
   * e.g. `addSbtPlugin("com.socrata" % "socrata-cloudbees-sbt" % "1.3.3")`
1. Add `socrata-sbt-plugins` plugin, as described below
1. Remove any redundant plugins, listed below in *What's inside*

Build scope, usually `build.sbt` or `project/Build.scala`

1. Delete any imports and code references to `SocrataCloudbeesSbt`
   * e.g. `com.socrata.cloudbeessbt.SocrataCloudbeesSbt.socrataSettings(assembly = true)`
1. Remove any redundant library dependencies, listed below in *What's inside*

Continue the remaining usage guide

### Adding the plugin
Add the following lines to `./project/plugins.sbt` or equivalent sbt project build.
See also: sbt wiki [Using Plugins](http://www.scala-sbt.org/release/tutorial/Using-Plugins.html).
```
resolvers += "Socrata Cloudbees" at "https://repository-socrata-oss.forge.cloudbees.com/release"

addSbtPlugin("com.socrata" % "socrata-sbt-plugins" %"1.4.3")
```

### Requirements
Version number must be `version in ThisBuild := "vMAJOR.MINOR.PATCH"` stored in `version.sbt`

### Invoking the important bits
`sbt dependencyGraph +clean +test +package +assembly +publishLocal +webdav:publish +release`

### Common config options
test coverage settings can be adjusted as follows
```
coverageMinimum := 70,
coverageFailOnMinimum := false
```

## What's inside
### Plugins included
* CoreSettings (internal)
  * adds scala compiler options for static analysis
* Sbt-Scoverage [(GitHub)](https://github.com/scoverage/sbt-scoverage)
  * add test code coverage statistics
  * defaults minimum=80%, fail=true
  * wired to enable before every `test`
  * wired to disable before every `package` and `assembly`
* Sbt-Scripted [(eed3si9n)](http://eed3si9n.com/testing-sbt-plugins)
  * testing framework for sbt plugins
* ScalaStyle-Sbt-Plugin [(GitHub)](https://github.com/scalastyle/scalastyle-sbt-plugin)
  * static analysis
  * wired to run on main sources before `package` and `assembly`
  * wired to run on test sources before `test`
* Sbt-Dependency-Graph [(GitHub)](https://github.com/jrudolph/sbt-dependency-graph)
  * execute `dependencyGraph` to see a visual of imported libraries
* Sbt-Assembly [(GitHub)](https://github.com/sbt/sbt-assembly)
  * execute `assembly` to build a fat jar including all dependencies
* Sbt-Release [(GitHub)](https://github.com/sbt/sbt-release)
  * uses `./version.sbt` to manage [semantic version](http://semver.org/) tags of the format `vMAJOR.MINOR.PATCH`
  * execute `release` to run the standard release process
* Sbt-Mima-Plugin Migration Manager [(GitHub)](https://github.com/typesafehub/migration-manager)
  * execute `mimaReportBinaryIssues` to check syntactic binary compatibility
* ScalaTest [(ScalaTest)](http://scalatest.org/quick_start)
  * execute `test` to run the code under src/test
* WebDav4Sbt [(BitBucket)](https://bitbucket.org/diversit/webdav4sbt)
  * execute `webdav:publish` runs release steps include upload artifacts to WebDAV style repo
  * included as a local fork due to diversIT repo offline
* Cloudbees-Sbt [(GitHub)](https://github.com/timperrett/sbt-cloudbees-plugin)
  * included as a local fork from our previous repo [(GitHub)](https://github.com/socrata/socrata-cloudbees-sbt)
    1. Check for snapshot dependencies and warn if they are present
    1. Clean and run tests
    1. Approve the changelog since the last release
    1. Ask about the release version and next SNAPSHOT
    1. Commit the release version and tag appropriately
    1. Commit the new snapshot version
    1. Push these changes to the remote repository (prompts for confirmation)
  * does not publish artifacts, this is Cloudbees' responsibility.
  * changelog output to `./target/release-manifest` for ease compiling release reports
  * the only version control system allowed is Git
* Support scala 2.11 projects
* Sbt-BuildInfo [(GitHub)](https://github.com/sbt/sbt-buildinfo)
  * calling `compile` now automatically generates buildinfo.BuildInfo.scala
  * customize the package name in sbt e.g. `buildInfoPackage := "com.socrata.sbtplugins"`
  * easily populate a version http endpoint or whatnot by calling `BuildInfo.toJson`

### Work in progress

### Coming soon, maybe
* Sbt-Doctest [(GitHub)](https://github.com/tkawachi/sbt-doctest)
* Sbt-One-Log [(Implicitly)](http://notes.implicit.ly/post/103363035569/sbt-one-log-1-0-0)
* Sbt-Codacy-Coverage [(GitHub)](https://github.com/codacy/sbt-codacy-coverage)
* Investigate support for scala 2.12 projects
