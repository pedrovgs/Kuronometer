package com.github.pedrovgs.kuronometer

import cats.free.Free
import com.github.pedrovgs.kuronometer.KuronometerResults.{ConnectionError, KuronometerResult}
import com.github.pedrovgs.kuronometer.free.algebra.ReporterOps
import com.github.pedrovgs.kuronometer.free.algebra.ViewOps
import com.github.pedrovgs.kuronometer.free.domain.View.Message
import com.github.pedrovgs.kuronometer.free.domain._
import com.github.pedrovgs.kuronometer.free.interpreter.formatter.DurationFormatter.NanosecondsFormat.format
import com.github.pedrovgs.kuronometer.free.interpreter.formatter.SummaryBuildStageExecutionFormatter

object Kuronometer {

  private val kuronometerHeader = "== Kuronometer =="

  def reportBuildFinished[F[_]](buildExecution: BuildExecution, config: Config)
                               (implicit R: ReporterOps[F], V: ViewOps[F]) = {
    val filteredBuildExecution = filterBuildExecutionData(buildExecution, config)
    for {
      result <- reportBuildExecution(filteredBuildExecution, config)
      _ <- showReportResult(result, config)
    } yield result
  }

  def getTotalBuildExecutionSummary[F[_]](implicit R: ReporterOps[F], V: ViewOps[F]) = {
    for {
      totalSummary <- R.getTotalBuildExecution
      _ <- showBuildExecutionSummary(totalSummary)
    } yield totalSummary
  }

  def getTodayBuildExecutionSummary[F[_]](implicit R: ReporterOps[F], V: ViewOps[F]) = {
    for {
      todaySummary <- R.getTodayBuildExecution
      _ <- showBuildExecutionSummary(todaySummary)
    } yield todaySummary
  }

  private def reportBuildExecution[F[_]](buildExecution: BuildExecution, config: Config)
                                  (implicit R: ReporterOps[F]): Free[F, KuronometerResult[BuildExecution]] = {
    for {
      result <- if (config.reportDataRemotely) {
        R.reportBuildExecution(buildExecution, RemoteReport)
        R.reportBuildExecution(buildExecution, LocalReport)
      } else {
        R.reportBuildExecution(buildExecution, LocalReport)
      }
    } yield result
  }

  private def showReportResult[F[_]](result: KuronometerResult[BuildExecution], config: Config)
                              (implicit V: ViewOps[F]): Free[F, Message] = {
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

  private def showBuildExecutionSummary[F[_]](result: KuronometerResult[SummaryBuildStagesExecution])
                                       (implicit V: ViewOps[F]): Free[F, Message] = {
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
