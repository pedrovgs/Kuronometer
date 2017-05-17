package com.github.pedrovgs.kuronometer.free.interpreter

import cats.{Id, ~>}
import com.github.pedrovgs.kuronometer.app.KuronometerProgram
import com.github.pedrovgs.kuronometer.free.algebra.{ReporterOp, ViewOp}

class Interpreters(implicit reporterInterpreter: (ReporterOp ~> Id), viewInterpreter: (ViewOp ~> Id)) {

  val kuronometerInterpreter: KuronometerProgram ~> Id = reporterInterpreter or viewInterpreter


}
