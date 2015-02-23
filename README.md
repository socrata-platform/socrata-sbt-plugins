# socrata-sbt-plugins
A repository for plugins that can be used across projects.

## Usage
### Adding the plugin
Add the following lines to `./project/plugins.sbt` or equivalent sbt project build.
See also: sbt wiki [Using Plugins](http://www.scala-sbt.org/release/tutorial/Using-Plugins.html).
```
resolvers += "https://repository-socrata-oss.forge.cloudbees.com/release"

addSbtPlugin("com.socrata" % "socrata-sbt-plugins" %"1.4.1")
```

### Requirements
Version number must be `version in Thisbuild := "vMAJOR.MINOR.PATCH"` stored in `version.sbt`

### Invoking the important bits
`sbt dependencyGraph +clean +test +package +assembly +publishLocal +webdav:publish +release`

### Common config options
test coverage settings can be adjusted as follows
```
coverageMinimum := 80,
coverageFailOnMinimum := true
```

## What's inside
### Plugins included
* CoreSettings (internal)
  * adds scala compiler options for static analysis
* Sbt-Scoverage [(GitHub)](https://github.com/scoverage/sbt-scoverage)
  * add test code coverage statistics
  * defaults minimum=100% fail=false
  * wired to enable before every `test`
  * wired to disable before every `package`
* Sbt-Scripted [(eed3si9n)](http://eed3si9n.com/testing-sbt-plugins)
  * testing framework for sbt plugins
* ScalaStyle-Sbt-Plugin [(GitHub)](https://github.com/scalastyle/scalastyle-sbt-plugin)
  * static analysis
  * wired to run on main sources before `compile`
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

### Work in progress
* Support scala 2.11 projects

### Coming soon, maybe
* Sbt-BuildInfo [(GitHub)](https://github.com/sbt/sbt-buildinfo)
* Sbt-Doctest [(GitHub)](https://github.com/tkawachi/sbt-doctest)
* Sbt-One-Log [(Implicitly)](http://notes.implicit.ly/post/103363035569/sbt-one-log-1-0-0)
* Sbt-Codacy-Coverage [(GitHub)](https://github.com/codacy/sbt-codacy-coverage)
