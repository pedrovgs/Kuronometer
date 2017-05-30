package com.github.pedrovgs.kuronometer

import cats.Eval
import cats.implicits._
import cats.free.Free
import com.github.pedrovgs.kuronometer.KuronometerResults.{
  ConnectionError,
  KuronometerResult,
  UnknownError
}
import com.github.pedrovgs.kuronometer.free.algebra.{ReporterOps, ViewOps}
import com.github.pedrovgs.kuronometer.free.domain.View.Message
import com.github.pedrovgs.kuronometer.free.domain._
import com.github.pedrovgs.kuronometer.free.interpreter.formatter.DurationFormatter.NanosecondsFormat.format
import com.github.pedrovgs.kuronometer.free.interpreter.formatter.SummaryBuildStageExecutionFormatter

object Kuronometer {

  private val kuronometerHeader = "== Kuronometer =="

  def reportBuildFinished[F[_]](buildExecution: BuildExecution,
                                config: Config)(
      implicit R: ReporterOps[F],
      V: ViewOps[F]): Free[F, KuronometerResult[BuildExecution]] = {
    val filteredBuildExecution =
      filterBuildExecutionData(buildExecution, config)
    reportBuildExecution(filteredBuildExecution, config)
      .flatMap(
        result =>
          showReportResult(result, config)
            .map(_ => result))
  }

  def getTotalBuildExecutionSummary[F[_]](
      implicit R: ReporterOps[F],
      V: ViewOps[F]): Free[F, KuronometerResult[SummaryBuildStagesExecution]] =
    R.getTotalBuildExecution
      .flatMap(
        summary =>
          showBuildExecutionSummary(summary)
            .map(_ => summary))

  def getTodayBuildExecutionSummary[F[_]](
      implicit R: ReporterOps[F],
      V: ViewOps[F]): Free[F, KuronometerResult[SummaryBuildStagesExecution]] =
    R.getTodayBuildExecution
      .flatMap(
        summary =>
          showBuildExecutionSummary(summary)
            .map(_ => summary))

  private def reportBuildExecution[F[_]](
      buildExecution: BuildExecution,
      config: Config)(implicit R: ReporterOps[F])
    : Free[F, KuronometerResult[BuildExecution]] = {
    val remoteReport = R.reportBuildExecution(buildExecution, RemoteReport)
    val localReport = R.reportBuildExecution(buildExecution, LocalReport)
    if (config.reportDataRemotely) {
      remoteReport >> localReport
    } else {
      localReport
    }
  }

  private def showReportResult[F[_]](
      result: KuronometerResult[BuildExecution],
      config: Config)(implicit V: ViewOps[F]): Free[F, Message] = {
    if (!config.verbose) {
      V.showMessage("")
    } else {
      result match {
        case Right(_) =>
          V.showSuccess(kuronometerHeader) >> V.showSuccess(
            "Kuronometer: build execution reported!")
        case Left(ConnectionError) =>
          V.showError(kuronometerHeader) >> V.showError(
            "Kuronometer: connection error reporting build execution to our servers :(")
        case _ =>
          V.showError(kuronometerHeader) >> V.showError(
            "Kuronometer: unknown error reporting build execution to our servers :(")
      }
    }
  }

  private def showBuildExecutionSummary[F[_]](
      result: KuronometerResult[SummaryBuildStagesExecution])(
      implicit V: ViewOps[F]): Free[F, Message] = {
    result match {
      case Right(summary) =>
        for {
          _ <- V.showMessage(kuronometerHeader)
          message <- V.showMessage(
            "Build time: " + format(summary.executionTimeInNanoseconds))
          _ <- V.showMessage(
            SummaryBuildStageExecutionFormatter.format(summary))
        } yield message
      case Left(UnknownError(Some(t))) =>
        V.showError(
            "Kuronometer: Exception catch while gathering data related to your builds execution: " + t.getLocalizedMessage + ".\n")
          .flatMap(_ =>
            V.showError(
              "Try cleaning your project executing \"./gradlew clean\". If this doesn't fix your problem open an issue in the project GitHub repository https://github.com/pedrovgs/kuronometer"))
      case Left(_) =>
        V.showError(
          "Kuronometer: Error gathering data related to your builds execution.")
    }
  }

  private def filterBuildExecutionData(buildExecution: BuildExecution,
                                       config: Config): BuildExecution =
    if (config.reportProjectInfo) {
      buildExecution
    } else {
      buildExecution.copy(project = None)
    }
}
