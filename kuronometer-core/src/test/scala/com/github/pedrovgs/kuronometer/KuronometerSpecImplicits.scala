package com.github.pedrovgs.kuronometer

import cats.free.Inject
import com.github.pedrovgs.kuronometer.free.algebra.{ReporterOp, ReporterOps, ViewOp, ViewOps}
import com.github.pedrovgs.kuronometer.free.interpreter._
import com.github.pedrovgs.kuronometer.free.interpreter.api.KuronometerApiClient
import com.github.pedrovgs.kuronometer.free.interpreter.csv.CsvReporter
import org.scalamock.scalatest.MockFactory

object KuronometerSpecImplicits extends MockFactory {

  implicit def R[F[_]](implicit I: Inject[ReporterOp, F]): ReporterOps[F] = new ReporterOps[F]

  implicit def V[F[_]](implicit I: Inject[ViewOp, F]): ViewOps[F] = new ViewOps[F]

  implicit val clock: Clock = mock[Clock]
  implicit val apiClient: KuronometerApiClient = mock[KuronometerApiClient]
  implicit val csvReporter: CsvReporter = mock[CsvReporter]

  implicit def viewInterpreter = new SilentViewInterpreter()

  implicit def reporterInterpreter = new ReporterInterpreter()

  implicit def interpreters = new Interpreters()
}
