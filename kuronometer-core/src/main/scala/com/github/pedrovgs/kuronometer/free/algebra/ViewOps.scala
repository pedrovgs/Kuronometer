package com.github.pedrovgs.kuronometer.free.algebra

import cats.free.{Free, Inject}
import com.github.pedrovgs.kuronometer.free.domain.View.Message

sealed trait ViewOp[A]

final case class ShowMessage(message: Message) extends ViewOp[Message]
final case class ShowSuccess(message: Message) extends ViewOp[Message]
final case class ShowError(message: Message) extends ViewOp[Message]

class ViewOps[F[_]](implicit I: Inject[ViewOp, F]) {

  def showMessage[A](message: Message): Free[F, Message] = {
    Free.inject[ViewOp, F](ShowMessage(message))
  }

  def showSuccess[A](message: Message): Free[F, Message] = {
    Free.inject[ViewOp, F](ShowSuccess(message))
  }

  def showError[A](message: Message): Free[F, Message] = {
    Free.inject[ViewOp, F](ShowError(message))
  }
}


