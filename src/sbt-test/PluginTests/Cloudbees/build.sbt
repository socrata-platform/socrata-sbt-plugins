import com.socrata.sbtplugins.CloudbeesPlugin.cloudbeesReleaseSteps
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._

val checkOriginalReleaseSteps = TaskKey[Unit]("checkOriginalReleaseSteps")
checkOriginalReleaseSteps := {
  val expected = Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
  val rs = releaseProcess.value
  val msg = "found release steps = %s".format(rs)
  if (rs == expected) {
    state.value.log.info(msg)
  } else {
    throw new Exception(msg)
  }
}

val checkCloudbeesReleaseSteps = TaskKey[Unit]("checkCloudbeesReleaseSteps")
checkCloudbeesReleaseSteps := {
  val expected = cloudbeesReleaseSteps
  val rs = releaseProcess.value
  val msg = "found release steps = %s".format(rs)
  if (rs == expected) {
    state.value.log.info(msg)
  } else {
    throw new Exception(msg)
  }
}
