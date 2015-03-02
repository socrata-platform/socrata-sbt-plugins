package com.socrata.sbtplugins

import java.io.File

import sbt.ProcessBuilder
import sbtrelease.Git

class GitMock(override val baseDir: File) extends Git(baseDir) {
  override val commandName: String = "echo"

  override def isBehindRemote: Boolean = ???

  override def currentHash: String = ???

  override def trackingRemote: String = ???

  override def currentBranch: String = ???

  override def hasUpstream: Boolean = ???

  override def pushChanges: ProcessBuilder = ???

  override def checkRemote(remote: String): ProcessBuilder = ???

  override def tag(name: String, comment: String, force: Boolean): ProcessBuilder = ???

  override def existsTag(name: String): Boolean = ???

  override def status: ProcessBuilder = ???
}
