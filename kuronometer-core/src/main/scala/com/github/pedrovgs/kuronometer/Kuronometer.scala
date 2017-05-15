package com.github.pedrovgs.kuronometer

import cats.free.Free
import com.github.pedrovgs.kuronometer.KuronometerResults.{ConnectionError, KuronometerResult}
import com.github.pedrovgs.kuronometer.app.KuronometerProgram
import com.github.pedrovgs.kuronometer.free.algebra.{ReporterOps, ViewOps}
import com.github.pedrovgs.kuronometer.free.domain.View.Message
import com.github.pedrovgs.kuronometer.free.domain._
import com.github.pedrovgs.kuronometer.free.interpreter.Interpreters
import com.github.pedrovgs.kuronometer.free.interpreter.formatter.DurationFormatter.NanosecondsFormat.format
import com.github.pedrovgs.kuronometer.free.interpreter.formatter.SummaryBuildStageExecutionFormatter

class Kuronometer(implicit R: ReporterOps[KuronometerProgram], V: ViewOps[KuronometerProgram], interpreters: Interpreters) {

  private val kuronometerHeader = "== Kuronometer =="

  def reportBuildFinished(buildExecution: BuildExecution, config: Config): KuronometerResult[BuildExecution] = {
    val filteredBuildExecution = filterBuildExecutionData(buildExecution, config)
    val program = for {
      result <- reportBuildExecution(filteredBuildExecution, config)
      _ <- showReportResult(result, config)
    } yield result
    program.foldMap(interpreters.kuronometerInterpreter)
  }

  def getTotalBuildExecutionSummary: KuronometerResult[SummaryBuildStagesExecution] = {
    val program = for {
      totalSummary <- R.getTotalBuildExecution
      _ <- showBuildExecutionSummary(totalSummary)
    } yield totalSummary
    program.foldMap(interpreters.kuronometerInterpreter)
  }

  def getTodayBuildExecutionSummary: KuronometerResult[SummaryBuildStagesExecution] = {
    val program = for {
      todaySummary <- R.getTodayBuildExecution
      _ <- showBuildExecutionSummary(todaySummary)
    } yield todaySummary
    program.foldMap(interpreters.kuronometerInterpreter)
  }

  private def reportBuildExecution(buildExecution: BuildExecution, config: Config): Free[KuronometerProgram, KuronometerResult[BuildExecution]] = {
    for {
      result <- if (config.reportDataRemotely) {
        R.reportBuildExecution(buildExecution, RemoteReport)
        R.reportBuildExecution(buildExecution, LocalReport)
      } else {
        R.reportBuildExecution(buildExecution, LocalReport)
      }
    } yield result
  }

  private def showReportResult(result: KuronometerResult[BuildExecution], config: Config): Free[KuronometerProgram, Message] = {
    if (!config.verbose) {
      V.showMessage("")
    } else {
      result match {
        case Right(_) => for {
          _ <- V.showSuccess(kuronometerHeader)
          message <- V.showSuccess("Kuronometer: build execution reported!")
        } yield message
        case Left(ConnectionError) => for {
          _ <- V.showError(kuronometerHeader)
          message <- V.showError("Kuronometer: connection error reporting build execution to our servers :(")
        } yield message
        case _ => for {
          _ <- V.showError(kuronometerHeader)
          message <- V.showError("Kuronometer: unknown error reporting build execution to our servers :(")
        } yield message
      }
    }
  }

  private def showBuildExecutionSummary(result: KuronometerResult[SummaryBuildStagesExecution]): Free[KuronometerProgram, Message] = {
    result match {
      case Right(summary) => for {
        _ <- V.showMessage(kuronometerHeader)
        message <- V.showMessage("Build time: " + format(summary.executionTimeInNanoseconds))
        _ <- V.showMessage(SummaryBuildStageExecutionFormatter.format(summary))
      } yield message
      case Left(_) => V.showError("Kuronometer: Error gathering data related to your builds execution.")
    }
  }

  private def filterBuildExecutionData(buildExecution: BuildExecution, config: Config): BuildExecution = {
    if (config.reportProjectInfo) {
      buildExecution
    } else {
      buildExecution.copy(project = None)
    }
  }
}
