package com.socrata.sbtplugins

import sbt._

/**
 * Socrata's wrapper around sbt-docker plugin.  This Plugin wraps an existing docker plugin and
 * adds additional functionality specific for our deployment process.
 *
 * References: See https://github.com/marcuslonnberg/sbt-docker
 */
object SocrataDockerPlugin extends AutoPlugin {

  /** See [[AutoPlugin.trigger]] */
  override def trigger: PluginTrigger = allRequirements

  /**
   * This Plugin requires the use of a base sbt-docker plugin.
   *
   * See [[AutoPlugin.requires]]
   */
  override def requires: Plugins = plugins.JvmPlugin  /* && DockerPlugin */

  /**
   * Socrata specific SBT build keys.
   */
  object SocrataDockerKeys {

    /**
     *
     */
    val commandLineArguments = settingKey[Seq[String]]("Application command line arguments.")

    /**
     *
     */
    val javaRunTimeArguments = settingKey[Seq[String]]("Java Runtime configuration variables.")

  }

  /**
   * TODO Add specific docker
   * @return
   */
  override def projectSettings: Seq[Def.Setting[_]] = super.projectSettings
}
