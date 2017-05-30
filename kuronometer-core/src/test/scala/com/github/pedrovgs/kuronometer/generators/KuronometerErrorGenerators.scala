package com.github.pedrovgs.kuronometer.generators

import com.github.pedrovgs.kuronometer.KuronometerResults.{
  ConnectionError,
  KuronometerError,
  UnknownError
}
import org.scalacheck.{Arbitrary, Gen}

object KuronometerErrorGenerators {

  implicit val arbKuronometerError: Arbitrary[KuronometerError] = Arbitrary(
    error)

  def error: Gen[KuronometerError] = Gen.oneOf(ConnectionError, UnknownError())

}
