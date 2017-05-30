package com.github.pedrovgs.kuronometer.free.interpreter.csv

import scala.beans.BeanProperty

case class CsvBuildStageExecution(@BeanProperty var name: String,
                                  @BeanProperty var executionTime: Long,
                                  @BeanProperty var timestamp: Long) {
  //This default empty constructor is needed by the Gradle plugins API.
  def this() = this("", 0, 0)
}
