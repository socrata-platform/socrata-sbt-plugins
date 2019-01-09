# socrata-sbt-plugins
A repository for plugins that can be used across projects.

## Usage
### Adding the plugin
Add the following lines to `./project/plugins.sbt` or equivalent sbt project build.
See also: sbt wiki [Using Plugins](http://www.scala-sbt.org/release/tutorial/Using-Plugins.html).
```
addSbtPlugin("com.socrata" % "socrata-sbt-plugins" % "[current version]")
```

### Requirements
Version number must be `version in ThisBuild := "vMAJOR.MINOR.PATCH"` stored in `version.sbt`

### Invoking the important bits
`sbt dependencyGraph +clean +test +package +assembly +publishLocal +webdav:publish +release`

### Common config options
test coverage settings can be adjusted as follows
```
import scoverage.ScoverageSbtPlugin.ScoverageKeys
ScoverageKeys.coverageMinimum := 70
ScoverageKeys.coverageFailOnMinimum := false
```

sometimes you might want to only warn on static analysis errors, try this:
```
// TODO: enable static analysis build failures
com.socrata.sbtplugins.StylePlugin.StyleKeys.styleFailOnError in Compile := false
com.socrata.sbtplugins.findbugs.JavaFindBugsPlugin.JavaFindBugsKeys.findbugsFailOnError in Compile := false,
com.socrata.sbtplugins.findbugs.JavaFindBugsPlugin.JavaFindBugsKeys.findbugsFailOnError in Test := false,
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
* Support scala 2.11 projects
* Sbt-BuildInfo [(GitHub)](https://github.com/sbt/sbt-buildinfo)
  * explicitly enable per project i.e. `enablePlugins(sbtbuildinfo.BuildInfoPlugin)`
  * automatically excluded from test coverage
  * calling `compile` now automatically generates buildinfo.BuildInfo.scala
  * customize the package name in sbt e.g. `buildInfoPackage := "com.socrata.sbtplugins"`
  * easily populate a version http endpoint or whatnot by calling `BuildInfo.toJson`

### Work in progress

### Coming soon, maybe
* Sbt-Doctest [(GitHub)](https://github.com/tkawachi/sbt-doctest)
* Sbt-One-Log [(Implicitly)](http://notes.implicit.ly/post/103363035569/sbt-one-log-1-0-0)
* Sbt-Codacy-Coverage [(GitHub)](https://github.com/codacy/sbt-codacy-coverage)
* Investigate support for scala 2.12 projects
