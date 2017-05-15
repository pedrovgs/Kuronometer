package com.github.pedrovgs.kuronometer.free.interpreter.csv

/* Due to interoperability issues between Groovy and Scala
 * this has to be declared as a mutable class with a default
 * constructor. We can't use a case class.
*/
case class CsvBuildStageExecution(var name: String, var executionTime: Long, var timestamp: Long) {

  def this() =  this("", 0, 0)

  def getName(): String = name

  def setName(name: String): Unit = this.name = name

  def getExecutionTime(): Long = executionTime

  def setExecutionTime(executionTime: Long): Unit = this.executionTime = executionTime

  def getTimestamp(): Long = timestamp

  def setTimestamp(timestamp: Long): Unit = this.timestamp = timestamp


}
