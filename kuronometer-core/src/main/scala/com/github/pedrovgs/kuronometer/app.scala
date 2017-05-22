package com.github.pedrovgs.kuronometer

import cats.data.Coproduct
import com.github.pedrovgs.kuronometer.free.algebra.{ReporterOp, ViewOp}

object app {
  type KuronometerProgram[A] = Coproduct[ReporterOp, ViewOp, A]
}
