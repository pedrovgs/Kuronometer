package com.github.pedrovgs.kuronometer.free.algebra

import cats.free.{Free, Inject}
import com.github.pedrovgs.kuronometer.KuronometerResults.KuronometerResult
import com.github.pedrovgs.kuronometer.free.domain.{
  BuildExecution,
  Report,
  SummaryBuildStagesExecution
}

sealed trait ReporterOp[A]

final case class ReportBuildExecution(buildExecution: BuildExecution,
                                      report: Report)
    extends ReporterOp[KuronometerResult[BuildExecution]]
final case class GetTotalBuildExecution()
    extends ReporterOp[KuronometerResult[SummaryBuildStagesExecution]]
final case class GetTodayBuildExecution()
    extends ReporterOp[KuronometerResult[SummaryBuildStagesExecution]]

class ReporterOps[F[_]](implicit I: Inject[ReporterOp, F]) {

  def reportBuildExecution[A](
      buildExecution: BuildExecution,
      reporter: Report): Free[F, KuronometerResult[BuildExecution]] =
    Free.inject[ReporterOp, F](ReportBuildExecution(buildExecution, reporter))

  def getTotalBuildExecution[A]
    : Free[F, KuronometerResult[SummaryBuildStagesExecution]] =
    Free.inject[ReporterOp, F](GetTotalBuildExecution())

  def getTodayBuildExecution[A]
    : Free[F, KuronometerResult[SummaryBuildStagesExecution]] =
    Free.inject[ReporterOp, F](GetTodayBuildExecution())

}

object ReporterOps {
  implicit def R[F[_]](implicit I: Inject[ReporterOp, F]): ReporterOps[F] =
    new ReporterOps[F]
}
