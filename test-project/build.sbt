import com.socrata.sbtplugins._

name := "test-project"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(HelloWorldPlugin, CoreSettingsPlugin)