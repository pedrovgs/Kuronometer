package com.github.pedrovgs.kuronometer.free.interpreter.csv

import java.io.File

import com.github.pedrovgs.kuronometer.free.domain.{BuildExecution, SummaryBuildStageExecution, SummaryBuildStagesExecution}
import com.github.pedrovgs.kuronometer.generators.BuildExecutionGenerators.{buildExecution, _}
import org.scalacheck.Gen
import org.scalatest.prop.PropertyChecks
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class CsvReporterSpec extends FlatSpec with Matchers with PropertyChecks with BeforeAndAfterEach {

  private val reporter = new CsvReporter()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    clearReporterFile
  }

  "CsvReporter" should "return an empty summary build stages execution if there are no previous builds persisted" in {
    val stages = reporter.getTotalBuildExecutionStages
    stages shouldBe Right(SummaryBuildStagesExecution())
  }

  it should "return as many stages as previously persisted getting the total build execution stages" in {
    forAll { (buildExecution: BuildExecution) =>
      clearReporterFile
      reporter.report(buildExecution)
      val reportedStages = buildExecution.buildStagesExecution.stages
      val stages = reporter.getTotalBuildExecutionStages.map(summary => summary.buildStages)
      stages shouldBe Right(reportedStages.map(reportedStage => SummaryBuildStageExecution(reportedStage.info.name, reportedStage.execution.executionTimeInNanoseconds, reportedStage.execution.timestamp)))
    }
  }

  it should "filter build stage executions by execution timestamp getting build execution stages by timestamp" in {
    forAll(buildExecution(Gen.choose(0L, 1000L))) { (buildExecution: BuildExecution) =>
      clearReporterFile
      reporter.report(buildExecution)
      val timestamp = 500
      val filteredBuildExecutions = reporter.getBuildExecutionStagesSinceTimestamp(timestamp)
      val expectedBuildExecutions = buildExecution.buildStagesExecution.stages.filter(timestamp <= _.execution.timestamp).map(reportedStage => SummaryBuildStageExecution(reportedStage.info.name, reportedStage.execution.executionTimeInNanoseconds, reportedStage.execution.timestamp))
      filteredBuildExecutions shouldBe Right(SummaryBuildStagesExecution(expectedBuildExecutions))
    }
  }

  it should "count build stages with the exact timestamp as part of the filtered build stages" in {
    val filterTimestamp = 1000
    forAll(buildExecution(Gen.const(filterTimestamp))) { (buildExecution: BuildExecution) =>
      clearReporterFile
      reporter.report(buildExecution)
      val timestamp = filterTimestamp
      val filteredBuildExecutions = reporter.getBuildExecutionStagesSinceTimestamp(timestamp)
      val expectedBuildExecutions = buildExecution.buildStagesExecution.stages.map(reportedStage => SummaryBuildStageExecution(reportedStage.info.name, reportedStage.execution.executionTimeInNanoseconds, reportedStage.execution.timestamp))
      filteredBuildExecutions shouldBe Right(SummaryBuildStagesExecution(expectedBuildExecutions))
    }
  }


  private def clearReporterFile = {
    val reportsFile = new File(CsvReporterConfig.executionTasksCsvFile)
    if (reportsFile.exists()) {
      reportsFile.delete()
    }
  }
}
