package com.github.pedrovgs.kuronometer

object KuronometerResults {

  type KuronometerResult[A] = Either[KuronometerError, A]

  sealed trait KuronometerError
  case object ConnectionError extends KuronometerError
  case class UnknownError(t: Option[Throwable] = None) extends KuronometerError

}
