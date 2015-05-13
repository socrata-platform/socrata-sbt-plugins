import scala.io.Source

val gitInit = TaskKey[Unit]("gitInit")
gitInit := {
  Process(Seq("git", "init")).!!
  Process(Seq("git", "commit", "--allow-empty", "-m", "initial commit")).!!
}

val buildInfoSrc = "target/scala-2.10/src_managed/main/sbt-buildinfo/BuildInfo.scala"
def buildInfoContains(path: String, string: String, log: Logger): Unit = {
  val lines = Source.fromFile(new File(path), "utf-8").getLines().toList
  log.info("BuildInfo generated source contents: ")
  log.info(lines.mkString("\n"))
  if (lines.count(_.contains(string)) > 0) {
    log.info(s"File($path) contains String($string)")
  } else {
    throw new RuntimeException(s"File($path) does not contain String($string)")
  }
}

val buildInfoContainsGitError = TaskKey[Unit]("buildInfoContainsGitError")
buildInfoContainsGitError := buildInfoContains(buildInfoSrc, "git error", state.value.log)

val buildInfoContainsPackage = TaskKey[Unit]("buildInfoContainsPackage")
buildInfoContainsPackage := buildInfoContains(buildInfoSrc, "package buildinfo", state.value.log)
