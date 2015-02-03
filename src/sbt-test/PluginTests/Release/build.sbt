import sbtrelease.ReleaseStep
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._

releaseProcess := Seq[ReleaseStep](runTest)
