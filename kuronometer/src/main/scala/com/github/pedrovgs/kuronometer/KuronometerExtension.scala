package com.github.pedrovgs.kuronometer

/* Due to interoperability issues between Groovy and Scala
 * this has to be declared as a mutable class with a default
 * constructor. We can't use a case class.
*/
object KuronometerExtension {
  val name = "kuronometer"
}

class KuronometerExtension(var platformName: String,
                           var reportProjectInfo: Boolean,
                           var reportDataRemotely: Boolean,
                           var verbose: Boolean) {

  def this() = this("Java", true, true, false)

  def getPlatformName: String = platformName

  def setPlatformName(platformName: String): Unit = this.platformName = platformName

  def getReportProjectInfo: Boolean = reportProjectInfo

  def setReportProjectInfo(reportProjectInfo: Boolean): Unit = this.reportProjectInfo = reportProjectInfo

  def getReportDataRemotely: Boolean = reportDataRemotely

  def setReportDataRemotely(reportDataRemotely: Boolean): Unit = this.reportDataRemotely = reportDataRemotely

  def getVerbose: Boolean = verbose

  def setVerbose(verbose: Boolean): Unit = this.verbose = verbose
}
