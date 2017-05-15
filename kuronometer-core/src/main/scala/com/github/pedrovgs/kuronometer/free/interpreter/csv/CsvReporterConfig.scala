package com.github.pedrovgs.kuronometer.free.interpreter.csv

import java.io.File

import org.supercsv.cellprocessor.ParseLong
import org.supercsv.cellprocessor.ift.CellProcessor

object CsvReporterConfig {
  private[csv] val headers = Array[String](
    "name",
    "executionTime",
    "timestamp")
  private[csv] val processors = Array[CellProcessor](
    new org.supercsv.cellprocessor.constraint.NotNull(),
    new ParseLong(),
    new ParseLong())
}
case class CsvReporterConfig(reportsFolder: String = "build/reports/kuronometer") {

  val executionTasksCsvFile: String = new File(reportsFolder).getAbsolutePath + "/tasksExecutionTimes.csv"

}
