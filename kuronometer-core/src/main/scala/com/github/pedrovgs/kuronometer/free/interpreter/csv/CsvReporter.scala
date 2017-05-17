package com.github.pedrovgs.kuronometer.free.interpreter.csv

import java.io.{File, FileReader, FileWriter}

import com.github.pedrovgs.kuronometer.KuronometerResults.KuronometerResult
import com.github.pedrovgs.kuronometer.free.domain._
import org.supercsv.io.{CsvBeanReader, CsvBeanWriter, ICsvBeanWriter}
import org.supercsv.prefs.CsvPreference

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class CsvReporter {

  def report(buildExecution: BuildExecution): KuronometerResult[BuildExecution] = {
    val stages = buildExecution.buildStagesExecution.stages
    writeBuildStages(stages)
    Right(buildExecution)
  }

  def getTotalBuildExecutionStages: KuronometerResult[SummaryBuildStagesExecution] = {
    getBuildExecutionStagesSinceTimestamp(0)
  }

  def getBuildExecutionStagesSinceTimestamp(filterTimestamp: Long): KuronometerResult[SummaryBuildStagesExecution] = {
    if (!existsReportFile || reportFileIsEmpty) {
      Right(SummaryBuildStagesExecution())
    } else {

      val csvBuildStages = mutable.ListBuffer[CsvBuildStageExecution]()

      val csvFile = new File(CsvReporterConfig.executionTasksCsvFile)
      val beanReader = new CsvBeanReader(new FileReader(csvFile), CsvPreference.STANDARD_PREFERENCE)
      val headers = beanReader.getHeader(true)
      try {
        var csvBuildStage: CsvBuildStageExecution = beanReader.read(classOf[CsvBuildStageExecution], headers, CsvReporterConfig.processors: _*)
        while (csvBuildStage != null) {
          if (csvBuildStage.timestamp >= filterTimestamp) {
            csvBuildStages.append(csvBuildStage)
          }
          csvBuildStage = beanReader.read(classOf[CsvBuildStageExecution], headers, CsvReporterConfig.processors: _*)
        }
      } finally if (beanReader != null) {
        beanReader.close()
      }
      Right(mapCsvBuildStages(csvBuildStages))
    }
  }

  def mapCsvBuildStages(csvBuildStages: ListBuffer[CsvBuildStageExecution]): SummaryBuildStagesExecution = {
    val stages = csvBuildStages.map { stage =>
      SummaryBuildStageExecution(stage.name, stage.executionTime, stage.timestamp)
    }
    SummaryBuildStagesExecution(stages)
  }

  def clear: Unit = {
    if (existsReportFile) {
      val csvFile = new File(CsvReporterConfig.executionTasksCsvFile)
      csvFile.delete()
    }
  }

  private def writeBuildStages(stages: Seq[BuildStage]) = {
    val csvStages: Seq[CsvBuildStageExecution] = mapBuildStages(stages)
    var beanWriter: ICsvBeanWriter = null
    try {
      val reportsFolder = new File(CsvReporterConfig.reportsFolder)
      if (!reportsFolder.exists()) {
        reportsFolder.mkdirs()
      }
      val csvFileExists = existsReportFile
      val writer = new FileWriter(CsvReporterConfig.executionTasksCsvFile, csvFileExists)
      beanWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE)
      if (!csvFileExists && csvStages.nonEmpty) {
        beanWriter.writeHeader(CsvReporterConfig.headers: _*)
      }
      for (stage <- csvStages) {
        beanWriter.write(stage, CsvReporterConfig.headers, CsvReporterConfig.processors)
      }
    } finally if (beanWriter != null) {
      beanWriter.close()
    }
  }

  private def mapBuildStages(stages: Seq[BuildStage]) = {
    val csvStages = stages.map { stage =>
      val name = stage.info.name
      val executionTime = stage.execution.executionTimeInNanoseconds
      val timestamp = stage.execution.timestamp
      CsvBuildStageExecution(name, executionTime, timestamp)
    }
    csvStages
  }

  private def existsReportFile = new File(CsvReporterConfig.executionTasksCsvFile).exists

  private def reportFileIsEmpty = new File(CsvReporterConfig.executionTasksCsvFile).length() == 0
}
