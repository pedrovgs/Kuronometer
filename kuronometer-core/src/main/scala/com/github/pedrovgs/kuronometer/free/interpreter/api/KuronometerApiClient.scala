package com.github.pedrovgs.kuronometer.free.interpreter.api

import com.github.pedrovgs.kuronometer.KuronometerResults.{ConnectionError, KuronometerResult, UnknownError}
import com.github.pedrovgs.kuronometer.free.domain.{BuildExecution, Platform}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import net.liftweb.json.ext.EnumNameSerializer

import scalaj.http.Http

class KuronometerApiClient(implicit apiClientConfig: KuronometerApiClientConfig) {

  private implicit val formats = DefaultFormats + new EnumNameSerializer(Platform)

  def report(buildExecution: BuildExecution): KuronometerResult[BuildExecution] = {
    val json = write(buildExecution)
    sendPostRequest(buildExecution, json, "/buildExecution")
  }

  private def sendPostRequest(buildExecution: BuildExecution, body: String, path: String): KuronometerResult[BuildExecution] = {
    try {
      val response = Http(composeUrl(path))
        .headers(KuronometerApiClientConfig.headers)
        .postData(body)
        .asString
      if (response.isSuccess) {
        Right(buildExecution)
      } else {
        Left(UnknownError)
      }
    } catch {
      case _: Throwable => {
        Left(ConnectionError)
      }
    }
  }

  private def composeUrl(path: String): String = apiClientConfig.scheme + "://" + apiClientConfig.host + ":" + apiClientConfig.port + path
}
