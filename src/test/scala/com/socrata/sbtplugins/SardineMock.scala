package com.socrata.sbtplugins

import java.io.InputStream
import java.util

import com.googlecode.sardine.{DavResource, Sardine}

class SardineMock extends Sardine {
  var urls: Map[String, Either[Throwable,Boolean]] = Map()

  override def getResources(url: String): util.List[DavResource] = ???

  override def move(sourceUrl: String, destinationUrl: String): Unit = ???

  override def enableCompression(): Unit = ???

  override def createDirectory(url: String): Unit =
    urls += url -> Right(true)

  override def disableCompression(): Unit = ???

  override def isCompressionEnabled: Boolean = ???

  override def put(url: String, data: Array[Byte]): Unit = ???

  override def put(url: String, dataStream: InputStream): Unit = ???

  override def put(url: String, data: Array[Byte], contentType: String): Unit = ???

  override def put(url: String, dataStream: InputStream, contentType: String): Unit = ???

  override def setCustomProps(url: String, addProps: util.Map[String, String], removeProps: util.List[String]): Unit =
    ???

  override def copy(sourceUrl: String, destinationUrl: String): Unit = ???

  override def delete(url: String): Unit = ???

  override def exists(url: String): Boolean =
    urls.getOrElse(url, Right(false)) match {
      case Left(e: Throwable) => throw e
      case Right(b: Boolean) => b
    }

  override def getInputStream(url: String): InputStream = ???
}
