package com.github.pedrovgs.kuronometer.free.interpreter.api

case class KuronometerApiClientConfig(scheme: String = "https",
                                      host: String = "kuronometer.io",
                                      port: Option[Int] = None)

object KuronometerApiClientConfig {
  private[api] val headers: Map[String, String] =
    Map[String, String]("Content-Type" -> "application/json; charset=UTF-8")
}
