name := "socrata-sbt-plugins-webdav-test"

publishTo <<= isSnapshot {s =>
  if (s) {Some("socrata snapshot"  at "https://repo.socrata.com/libs-snapshot-local")}
  else {Some("socrata release"   at "https://repo.socrata.com/libs-release-local")}
}
credentials += Credentials(file("/private/socrata-oss/maven-credentials"))
