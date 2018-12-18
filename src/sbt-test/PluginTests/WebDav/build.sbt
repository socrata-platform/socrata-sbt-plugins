name := "socrata-sbt-plugins-webdav-test"

publishTo <<= isSnapshot {s =>
  if (s) {Some("socrata snapshot"  at "https://repo.socrata.com/libs-snapshot")}
  else {Some("socrata release"   at "https://repo.socrata.com/libs-release")}
}
credentials += Credentials(file("/private/socrata-oss/maven-credentials"))
