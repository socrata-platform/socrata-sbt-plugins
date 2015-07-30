package com.socrata.sbtplugins.findbugs

import edu.umd.cs.findbugs.DetectorFactoryCollection
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import sbt.File

import scala.xml._

// scalastyle:off number.of.types
case class JavaFindBugsXml(report: Option[BugCollection])
object JavaFindBugsXml {
  def apply(reportPath: File): JavaFindBugsXml = {
    val path = reportPath
    if (path.exists() && path.length() > 0) {
      val xml = XML.loadFile(path)
      JavaFindBugsXml(Some(BugCollection(xml)))
    } else { JavaFindBugsXml(None) }
  }
}

case class BugCollection(version: String,
                         sequence: Int,
                         timestamp: Long,
                         analysisTimestamp: Long,
                         release: String,
                         project: Project,
                         bugs: Seq[BugInstance],
                         errors: Errors,
                         summary: FindBugsSummary,
                         features: ClassFeatures,
                         history: History)
object BugCollection {
  def apply(xml: Elem): BugCollection = BugCollection(
    (xml \ "@version").text,
    (xml \ "@sequence").text.toInt,
    (xml \ "@timestamp").text.toLong,
    (xml \ "@analysisTimestamp").text.toLong,
    (xml \ "@release").text,
    Project((xml \ "Project").head),
    (xml \ "BugInstance").map(x => BugInstance(x)),
    Errors((xml \ "Errors").head),
    FindBugsSummary((xml \ "FindBugsSummary").head),
    ClassFeatures((xml \ "ClassFeatures").head),
    History((xml \ "History").head))
}

case class Project(projectName: String, jars: Seq[Jar], auxClasspaths: Seq[AuxClasspathEntry])
object Project {
  def apply(xml: Node): Project = Project(
    (xml \ "@projectName").text,
    (xml \ "Jar").map(x => Jar(x)),
    (xml \ "AuxClasspathEntry").map(x => AuxClasspathEntry(x)))
}

case class Jar(text: String)
object Jar {
  def apply(xml: Node): Jar = Jar(xml.text)
}

case class AuxClasspathEntry(text: String)
object AuxClasspathEntry {
  def apply(xml: Node): AuxClasspathEntry = AuxClasspathEntry(xml.text)
}

case class BugInstance(typ: String,
                       priority: Int,
                       rank: Int,
                       abbrev: String,
                       category: String,
                       clazz: Option[Clazz],
                       method: Option[Method],
                       localVariable: Option[LocalVariable],
                       sourceLine: Option[SourceLine]) {
  lazy val detector = {
    try {
      Some(DetectorFactoryCollection.instance())
    } catch {
      // TODO: fix findbugs initialization in test suite
      case e: ExceptionInInitializerError => None
      case e: NoClassDefFoundError => None
    }
  }
  def summarize: String = {
    val bug = detector.map(_.lookupBugPattern(typ))
    s"$category $abbrev $typ: ${clazz.map(_.summarize).getOrElse("<unknown source>")}. " +
      bug.map(_.getShortDescription).getOrElse("<unknown BugPattern>")
  }
}
object BugInstance {
  def apply(xml: Node): BugInstance = BugInstance(
    (xml \ "@type").text,
    (xml \ "@priority").text.toInt,
    (xml \ "@rank").text.toInt,
    (xml \ "@abbrev").text,
    (xml \ "@category").text,
    (xml \ "Class").map(x => Clazz(x)).headOption,
    (xml \ "Method").map(x => Method(x)).headOption,
    (xml \ "LocalVariable").map(x => LocalVariable(x)).headOption,
    (xml \ "SourceLine").map(x => SourceLine(x)).headOption)
}

case class Clazz(name: String, sourceLine: SourceLine) {
  def summarize: String = sourceLine.summarize
}
object Clazz {
  def apply(xml: Node): Clazz = Clazz(
    (xml \ "@classname").text,
    SourceLine((xml \ "SourceLine").head))
}

case class SourceLine(className: String, start: Option[Int], end: Option[Int], sourceFile: String, sourcePath: String) {
  def summarize: String = s"$sourcePath:${start.getOrElse(-1)}"
}
object SourceLine {
  def apply(xml: Node): SourceLine = SourceLine(
    (xml \ "@classname").text,
    (xml \ "@start").headOption.map(_.text.toInt),
    (xml \ "@end").headOption.map(_.text.toInt),
    (xml \ "@sourcefile").text,
    (xml \ "@sourcepath").text)
}

case class Method(className: String, name: String, signature: String, isStatic: Boolean, sourceLine: Option[SourceLine])
object Method {
  def apply(xml: Node): Method = Method(
    (xml \ "@classname").text,
    (xml \ "@name").text,
    (xml \ "@signature").text,
    (xml \ "@isStatic").text.toBoolean,
    (xml \ "SourceLine").headOption.map(x => SourceLine(x)))
}

case class LocalVariable(name: String, register: Int, pc: Int, role: String)
object LocalVariable {
  def apply(xml: Node): LocalVariable = LocalVariable(
    (xml \ "@name").text,
    (xml \ "@register").text.toInt,
    (xml \ "@pc").text.toInt,
    (xml \ "@role").text)
}

case class Errors(errors: Int, missingClasses: Int)
object Errors {
  def apply(xml: Node): Errors = Errors(
    (xml \ "@errors").text.toInt,
    (xml \ "@missingClasses").text.toInt)
}

case class FindBugsSummary(timestamp: DateTime,
                           totalClasses: Int,
                           referencedClasses: Int,
                           totalBugs: Int,
                           totalSize: Int,
                           numPackages: Int,
                           javaVersion: String,
                           vmVersion: String,
                           cpuSeconds: Float,
                           clockSeconds: Float,
                           peakMBytes: Float,
                           alocMBytes: Float,
                           gcSeconds: Float,
                           priority1: Option[Int],
                           packages: Seq[PackageStats],
                           profile: FindBugsProfile) {
  def summarize: String = {
    val sb = new StringBuilder
    sb.append(s"FindBugs Summary, ran $timestamp:\n")
    sb.append(s"  bugs: $totalBugs\n")
    sb.append(s"  java: $javaVersion, vm: $vmVersion\n")
    sb.append(s"  cpu time: $cpuSeconds s, gc time: $gcSeconds s, time elapsed: $clockSeconds s\n")
    sb.append(s"  megabytes peak: $peakMBytes, alloc: $alocMBytes\n")
    sb.append(s"  packages: $numPackages, classes: $totalClasses, referenced: $referencedClasses, size: $totalSize\n")
    sb.append(packages.map(_.summarize).mkString("    ", "\n    ", "\n"))
    sb.toString()
  }
}
object FindBugsSummary {
  def datetimeFormat: DateTimeFormatter = DateTimeFormat.forPattern("E, dd MMM yyyy HH:mm:ss Z")
  def apply(xml: Node): FindBugsSummary = FindBugsSummary(
    datetimeFormat.parseDateTime((xml \ "@timestamp").text),
    (xml \ "@total_classes").text.toInt,
    (xml \ "@referenced_classes").text.toInt,
    (xml \ "@total_bugs").text.toInt,
    (xml \ "@total_size").text.toInt,
    (xml \ "@num_packages").text.toInt,
    (xml \ "@java_version").text,
    (xml \ "@vm_version").text,
    (xml \ "@cpu_seconds").text.toFloat,
    (xml \ "@clock_seconds").text.toFloat,
    (xml \ "@peak_mbytes").text.toFloat,
    (xml \ "@alloc_mbytes").text.toFloat,
    (xml \ "@gc_seconds").text.toFloat,
    (xml \ "@priority_1").headOption.map(_.text.toInt),
    (xml \ "PackageStats").map(x => PackageStats(x)),
    FindBugsProfile((xml \ "FindBugsProfile").head))
}

case class PackageStats(name: String,
                        totalBugs: Int,
                        totalTypes: Int,
                        totalSize: Int,
                        priority1: Option[Int],
                        classes: Seq[ClassStats]) {
  def summarize: String = s"$name types:$totalTypes size:$totalSize bugs:$totalBugs"
  def summarizeDeep: String = {
    val sb = new StringBuilder
    sb.append(s"$summarize\n")
    classes.foreach(c => sb.append(s"  ${c.summarize}\n"))
    sb.toString()
  }
}
object PackageStats {
  def apply(xml: Node): PackageStats = PackageStats(
    (xml \ "@package").text,
    (xml \ "@total_bugs").text.toInt,
    (xml \ "@total_types").text.toInt,
    (xml \ "@total_size").text.toInt,
    (xml \ "@priority_1").headOption.map(_.text.toInt),
    (xml \ "ClassStats").map(x => ClassStats(x)))
}

case class ClassStats(name: String, sourceFile: String, interface: Boolean, size: Int, bugs: Int) {
  def summarize: String = s"$name size:$size bugs:$bugs"
}
object ClassStats {
  def apply(xml: Node): ClassStats = ClassStats(
    (xml \ "@class").text,
    (xml \ "@sourceFile").text,
    (xml \ "@interface").text.toBoolean,
    (xml \ "@size").text.toInt,
    (xml \ "@bugs").text.toInt)
}

case class FindBugsProfile(classProfiles: Seq[ClassProfile])
object FindBugsProfile {
  def apply(xml: Node): FindBugsProfile = FindBugsProfile(
    (xml \ "ClassProfile").map(x => ClassProfile(x)))
}

case class ClassProfile(name: String,
                        totalMilliseconds: Int,
                        invocations: Int,
                        avgMicrosecondsPerInvocation: Int,
                        maxMicrosecondsPerInvocation: Int,
                        standardDeviationMicrosecondsPerInvocation: Int)
object ClassProfile {
  def apply(xml: Node): ClassProfile = ClassProfile(
    (xml \ "@name").text,
    (xml \ "@totalMilliseconds").text.toInt,
    (xml \ "@invocations").text.toInt,
    (xml \ "@avgMicrosecondsPerInvocation").text.toInt,
    (xml \ "@maxMicrosecondsPerInvocation").text.toInt,
    (xml \ "@standardDeviationMircosecondsPerInvocation").text.toInt) // [sic]
}

case class ClassFeatures()
object ClassFeatures {
  def apply(xml: Node): ClassFeatures = ClassFeatures()
}

case class History()
object History {
  def apply(xml: Node): History = History()
}
