socrata-sbt-plugins
===================
A repository for plugins that can be used across projects.

Plugins included
----------------
* CoreSettings (internal)
  * adds scala compiler options for static analysis
* HelloWorld (internal) 
  * *will be removed*
* Sbt-Meow [(GitHub)](https://github.com/thricejamie/sbt-meow)
  * ascii prints a random cat picture
  * *will be removed* 
* Sbt-Scoverage [(GitHub)](https://github.com/scoverage/sbt-scoverage)
  * add test code coverage statistics
  * defaults minimum=100% fail=false
  * wired to enable before every ```test```
  * wired to disable before every ```package```
* Sbt-Scripted [(eed3si9n)](http://eed3si9n.com/testing-sbt-plugins)
  * testing framework for sbt plugins
* ScalaStyle-Sbt-Plugin [(GitHub)](https://github.com/scalastyle/scalastyle-sbt-plugin)
  * static analysis
  * wired to run on main sources before ```compile```
  * wired to run on test sources before ```test```

Coming soon, maybe
------------------
* Cloudbees-Sbt (internal) **or**
* Cloudbees-Sbt [(GitHub)](https://github.com/timperrett/sbt-cloudbees-plugin)
* Sbt-Dependency-Graph [(GitHub)](https://github.com/jrudolph/sbt-dependency-graph)
* Sbt-Assembly [(GitHub)](https://github.com/sbt/sbt-assembly)
* Sbt-Release [(GitHub)](https://github.com/sbt/sbt-release)
* Sbt-Mima-Plugin Migration Manager [(GitHub)](https://github.com/typesafehub/migration-manager)
* Sbt-BuildInfo [(GitHub)](https://github.com/sbt/sbt-buildinfo)
* ScalaTest [(ScalaTest)](http://scalatest.org/quick_start)
* Sbt-Doctest [(GitHub)](https://github.com/tkawachi/sbt-doctest)
* Sbt-One-Log [(Implicitly)](http://notes.implicit.ly/post/103363035569/sbt-one-log-1-0-0)
* Sbt-Codacy-Coverage [(GitHub)](https://github.com/codacy/sbt-codacy-coverage)

