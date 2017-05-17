package com.github.pedrovgs.kuronometer.free.interpreter.csv

import java.io.File

import org.supercsv.cellprocessor.ParseLong
import org.supercsv.cellprocessor.ift.CellProcessor

private [csv] object CsvReporterConfig {

  val headers: Array[String] = Array[String](
    "name",
    "executionTime",
    "timestamp")

  val processors: Array[CellProcessor] = Array[CellProcessor](
    new org.supercsv.cellprocessor.constraint.NotNull(),
    new ParseLong(),
    new ParseLong())

  val reportsFolder: String = "build/reports/kuronometer"
  val executionTasksCsvFile: String = new File(reportsFolder).getAbsolutePath + "/tasksExecutionTimes.csv"
}
