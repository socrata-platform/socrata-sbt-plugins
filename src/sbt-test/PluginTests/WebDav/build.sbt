name := "socrata-sbt-plugins-webdav-test"

publishTo <<= isSnapshot {s =>
  if (s) {Some("socrata snapshot"  at "https://repository-socrata-oss.forge.cloudbees.com/snapshot")}
  else {Some("socrata release"   at "https://repository-socrata-oss.forge.cloudbees.com/release")}
}
credentials += Credentials(file("/private/socrata-oss/maven-credentials"))
