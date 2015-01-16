val pv = System.getProperty("project.version")
if (pv == null) {
  throw new RuntimeException( """|The system property 'project.version' is not defined.
                                 |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
} else { addSbtPlugin("com.socrata" %% "socrata-sbt-plugins" % pv) }
