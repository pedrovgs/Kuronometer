package com.github.pedrovgs.kuronometer.free.interpreter

import cats.{Id, ~>}
import com.github.pedrovgs.kuronometer.free.algebra._
import com.github.pedrovgs.kuronometer.free.domain.View.Message

class ViewInterpreter extends (ViewOp ~> Id) {

  override def apply[A](fa: ViewOp[A]): Id[A] = fa match {
    case ShowMessage(message) => show(message)
    case ShowSuccess(message) => showSuccess(message)
    case ShowError(message) => showError(message)
  }

  private def show(message: Message): Message = {
    if (!message.isEmpty) {
      println(Console.CYAN + message + Console.RESET)
    }
    message
  }

  private def showSuccess(message: Message): Message = {
    show(Console.GREEN + message + Console.RESET)
    message
  }


  private def showError(message: Message): Message = {
    show(Console.RED + message + Console.RESET)
    message
  }
}
