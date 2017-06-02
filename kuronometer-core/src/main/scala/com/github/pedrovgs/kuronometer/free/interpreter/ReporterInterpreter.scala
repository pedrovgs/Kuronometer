package com.github.pedrovgs.kuronometer.free.interpreter

import cats.{Id, ~>}
import com.github.pedrovgs.kuronometer.free.algebra.{
  GetTodayBuildExecution,
  GetTotalBuildExecution,
  ReportBuildExecution,
  ReporterOp
}
import com.github.pedrovgs.kuronometer.free.domain.{LocalReport, RemoteReport}
import com.github.pedrovgs.kuronometer.free.interpreter.api.KuronometerApiClient
import com.github.pedrovgs.kuronometer.free.interpreter.csv.CsvReporter
import com.github.pedrovgs.kuronometer.implicits._

class ReporterInterpreter(implicit csvReporter: CsvReporter,
                          apiClient: KuronometerApiClient,
                          clock: Clock)
    extends (ReporterOp ~> Id) {

  override def apply[A](fa: ReporterOp[A]): Id[A] = fa match {
    case ReportBuildExecution(buildExecution, RemoteReport) =>
      apiClient.report(buildExecution)
      Right(buildExecution)
    case ReportBuildExecution(buildExecution, LocalReport) =>
      csvReporter.report(buildExecution)
    case GetTotalBuildExecution() => csvReporter.getTotalBuildExecutionStages
    case GetTodayBuildExecution() =>
      csvReporter.getBuildExecutionStagesSinceTimestamp(
        clock.todayMidnightTimestamp)
  }

}
