package com.github.pedrovgs.kuronometer.free.interpreter


import cats.{Id, ~>}
import com.github.pedrovgs.kuronometer.free.algebra._

class SilentViewInterpreter extends (ViewOp ~> Id) {

  override def apply[A](fa: ViewOp[A]): Id[A] = fa match {
    case ShowMessage(message) => message
    case ShowSuccess(message) => message
    case ShowError(message) => message
  }

}