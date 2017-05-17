package com.github.pedrovgs.kuronometer

import com.github.pedrovgs.kuronometer.free.interpreter._
import com.github.pedrovgs.kuronometer.free.interpreter.api.{KuronometerApiClient, KuronometerApiClientConfig}
import com.github.pedrovgs.kuronometer.free.interpreter.csv.CsvReporter
import org.scalamock.scalatest.MockFactory

object KuronometerSpecImplicits extends MockFactory {

  implicit val clock: Clock = mock[Clock]
  implicit val apiClient: KuronometerApiClient = mock[KuronometerApiClient]
  implicit val csvReporter: CsvReporter = mock[CsvReporter]

  implicit def viewInterpreter = new SilentViewInterpreter()

  implicit def reporterInterpreter = new ReporterInterpreter()

  implicit def interpreters = new Interpreters()
}
