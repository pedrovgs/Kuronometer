package com.github.pedrovgs.kuronometer

import com.github.pedrovgs.kuronometer.free.interpreter.api.{KuronometerApiClient, KuronometerApiClientConfig}
import com.github.pedrovgs.kuronometer.free.interpreter.csv.{CsvReporter, CsvReporterConfig}
import com.github.pedrovgs.kuronometer.free.interpreter.{Clock, Interpreters, ReporterInterpreter, ViewInterpreter}

object implicits {

  implicit def clock = new Clock()

  implicit def csvReporterConfig = new CsvReporterConfig()

  implicit def csvReporter = new CsvReporter()

  implicit def apiClientConfig = KuronometerApiClientConfig()

  implicit def apiClient = new KuronometerApiClient()

  implicit def viewInterpreter = new ViewInterpreter

  implicit def reporterInterpreter = new ReporterInterpreter()

  implicit def interpreters = new Interpreters()

}
