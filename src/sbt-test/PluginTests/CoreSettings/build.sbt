val checkScalaVersion = TaskKey[Unit]("checkScalaVersion")
checkScalaVersion := {
  val expected = "2.10.4"
  val sv = scalaVersion.value
  val msg = "found scalaVersion = %s".format(sv)
  if (sv == expected) {
    state.value.log.info(msg)
  } else {
    throw new Exception(msg)
  }
}

val checkScalacOptions = TaskKey[Unit]("checkScalacOptions")
checkScalacOptions := {
  val expected = Seq("-encoding", "UTF-8", "-Xlint", "-deprecation", "-Xfatal-warnings", "-unchecked", "-g:vars", "-feature")
  val sco = scalacOptions.value
  val msg = "found scalacOptions = %s".format(sco)
  if (sco == expected) {
    state.value.log.info(msg)
  } else {
    throw new Exception(msg)
  }
}
