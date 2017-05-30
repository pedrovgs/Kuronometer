package com.github.pedrovgs.kuronometer

import cats.free.Inject
import com.github.pedrovgs.kuronometer.free.algebra.{
  ReporterOp,
  ReporterOps,
  ViewOp,
  ViewOps
}
import com.github.pedrovgs.kuronometer.free.interpreter.api.{
  KuronometerApiClient,
  KuronometerApiClientConfig
}
import com.github.pedrovgs.kuronometer.free.interpreter.csv.{
  CsvReporter,
  CsvReporterConfig
}
import com.github.pedrovgs.kuronometer.free.interpreter.{
  Clock,
  Interpreters,
  ReporterInterpreter,
  ViewInterpreter
}

object implicits {

  implicit def R[F[_]](implicit I: Inject[ReporterOp, F]): ReporterOps[F] =
    new ReporterOps[F]
  implicit def V[F[_]](implicit I: Inject[ViewOp, F]): ViewOps[F] =
    new ViewOps[F]

  implicit def clock = new Clock()

  implicit def csvReporter = new CsvReporter()

  implicit def apiClientConfig = KuronometerApiClientConfig()

  implicit def apiClient = new KuronometerApiClient()

  implicit def viewInterpreter = new ViewInterpreter

  implicit def reporterInterpreter = new ReporterInterpreter()

  implicit def interpreters = new Interpreters()

}
