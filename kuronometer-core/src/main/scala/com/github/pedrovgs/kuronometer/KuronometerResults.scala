package com.github.pedrovgs.kuronometer

object KuronometerResults {

  type KuronometerResult[A] = Either[KuronometerError, A]

  sealed trait KuronometerError
  case object ConnectionError extends KuronometerError
  case object UnknownError extends KuronometerError

}
