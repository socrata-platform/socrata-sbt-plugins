scalaVersion := "2.11.4"

val checkScalaVersion = TaskKey[Unit]("checkScalaVersion")
checkScalaVersion := {
  val expected = "2.11.4"
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
  val compileExpected = Seq("-encoding", "UTF-8", "-Xlint", "-deprecation", "-Xfatal-warnings", "-unchecked", "-g:vars", "-feature", "-Ywarn-unused-import")
  val sco = scalacOptions.value
  val msg = "found scalacOptions = %s".format(sco)
  if (sco == compileExpected) {
    state.value.log.info(msg)
  } else {
    throw new Exception(msg)
  }
}

val checkScalacConsoleOptions = TaskKey[Unit]("checkScalacConsoleOptions")
checkScalacConsoleOptions := {
  val consoleExpected = Seq("-encoding", "UTF-8", "-Xlint", "-deprecation", "-Xfatal-warnings", "-unchecked", "-g:vars", "-feature")
  val sco = (scalacOptions in (Compile, console)).value
  val msg = "found scalacOptions in (Compile, console) = %s".format(sco)
  if (sco == consoleExpected) {
    state.value.log.info(msg)
  } else {
    throw new Exception(msg)
  }
}
