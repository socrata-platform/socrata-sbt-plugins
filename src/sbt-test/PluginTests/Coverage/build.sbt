resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"
libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.1" % "test"

import ScoverageSbtPlugin.ScoverageKeys._
import ScoverageSbtPlugin.enabled
coverageFailOnMinimum := true

lazy val checkCoverageIsEnabled = TaskKey[Unit]("checkCoverageIsEnabled")
checkCoverageIsEnabled := {
  if (enabled) {
    state.value.log.info("coverage is enabled")
  } else {
    throw new Exception("coverage is not enabled")
  }
}

lazy val checkCoverageIsDisabled = TaskKey[Unit]("checkCoverageIsDisabled")
checkCoverageIsDisabled := {
  if (!enabled) {
    state.value.log.info("coverage is disabled")
  } else {
    throw new Exception("coverage is not disabled")
  }
}
