name := "socrata-sbt-plugins-webdav-test"
organization := "com.socrata"
scalaVersion in Global := "2.10.4"

publishTo <<= isSnapshot {s =>
  if (s) {Some("socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot")}
  else {Some("socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release")}
}
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")
