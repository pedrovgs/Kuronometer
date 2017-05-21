package com.github.pedrovgs.kuronometer.free.interpreter.csv

import java.io.{File, FileReader, FileWriter}

import com.github.pedrovgs.kuronometer.KuronometerResults.KuronometerResult
import com.github.pedrovgs.kuronometer.free.domain.{SummaryBuildStagesExecution, _}
import org.supercsv.io.{CsvBeanReader, CsvBeanWriter, ICsvBeanWriter}
import org.supercsv.prefs.CsvPreference
import com.github.pedrovgs.kuronometer.KuronometerResults.UnknownError
import scala.annotation.tailrec
import scala.util.Try

object CsvReporter {
  private val emptySummary = Right(SummaryBuildStagesExecution())
}
class CsvReporter {

  import CsvReporter._

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
      emptySummary
    } else {
      var beanReader: CsvBeanReader = null
      Try {
        val csvFile = new File(CsvReporterConfig.executionTasksCsvFile)
        beanReader = new CsvBeanReader(new FileReader(csvFile), CsvPreference.STANDARD_PREFERENCE)
        val headers = beanReader.getHeader(true)
        val csvBuildStages = innerReadBuildStages(filterTimestamp, Seq(), headers, beanReader)
        Right(mapCsvBuildStages(csvBuildStages))
      }.recover {
        case _ =>
          beanReader.close()
          Left(UnknownError)
      }.toOption.getOrElse(emptySummary)
    }
  }

  def mapCsvBuildStages(csvBuildStages: Seq[CsvBuildStageExecution]): SummaryBuildStagesExecution = {
    val stages = csvBuildStages.map { stage =>
      SummaryBuildStageExecution(stage.name, stage.executionTime, stage.timestamp)
    }
    SummaryBuildStagesExecution(stages)
  }

  @tailrec
  private def innerReadBuildStages(filterTimestamp: Long, stages: Seq[CsvBuildStageExecution], headers: Array[String], beanReader: CsvBeanReader): Seq[CsvBuildStageExecution] = {
    val csvBuildStage = beanReader.read[CsvBuildStageExecution](classOf[CsvBuildStageExecution], headers, CsvReporterConfig.processors: _*)
    if (csvBuildStage == null) {
      beanReader.close()
      stages
    } else if (csvBuildStage.timestamp < filterTimestamp) {
      innerReadBuildStages(filterTimestamp, stages, headers, beanReader)
    } else {
      innerReadBuildStages(filterTimestamp, stages :+ csvBuildStage, headers, beanReader)
    }
  }

  private def writeBuildStages(stages: Seq[BuildStage]) = {
    var beanWriter: ICsvBeanWriter = null
    Try {
      val csvStages: Seq[CsvBuildStageExecution] = mapBuildStages(stages)
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
      innerWriteBuildStages(csvStages, beanWriter)
    }.recover {
      case _ =>
        beanWriter.close()
    }
  }

  @tailrec
  private def innerWriteBuildStages(stages: Seq[CsvBuildStageExecution], beanWriter: ICsvBeanWriter): Unit = {
    if (stages.nonEmpty) {
      beanWriter.write(stages.head, CsvReporterConfig.headers, CsvReporterConfig.processors)
      innerWriteBuildStages(stages.tail, beanWriter)
    } else {
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
