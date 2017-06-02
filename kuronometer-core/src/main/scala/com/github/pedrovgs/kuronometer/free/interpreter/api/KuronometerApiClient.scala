package com.github.pedrovgs.kuronometer.free.interpreter.api

import com.github.pedrovgs.kuronometer.KuronometerResults.{
  ConnectionError,
  KuronometerResult,
  UnknownError
}
import com.github.pedrovgs.kuronometer.free.domain.{BuildExecution, Platform}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write
import net.liftweb.json.ext.EnumNameSerializer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scalaj.http.Http

class KuronometerApiClient {

  private implicit val formats = DefaultFormats + new EnumNameSerializer(
    Platform)

  def report(buildExecution: BuildExecution)(
      implicit apiClientConfig: KuronometerApiClientConfig)
    : Future[KuronometerResult[BuildExecution]] = {
    val json = write(buildExecution)
    sendPostRequest(buildExecution, json, "/buildExecution")
  }

  private def sendPostRequest(
      buildExecution: BuildExecution,
      body: String,
      path: String)(implicit apiClientConfig: KuronometerApiClientConfig)
    : Future[KuronometerResult[BuildExecution]] = {
    Future {
      Try(
        Http(composeUrl(path))
          .headers(KuronometerApiClientConfig.headers)
          .postData(body)
          .asString)
        .map(response =>
          if (response.isSuccess) Right(buildExecution)
          else Left(UnknownError()))
        .toOption
        .getOrElse(Left(ConnectionError))
    }
  }

  private def composeUrl(path: String)(
      implicit apiClientConfig: KuronometerApiClientConfig): String =
    apiClientConfig.port match {
      case Some(port) =>
        apiClientConfig.scheme + "://" + apiClientConfig.host + ":" + port + path
      case _ => apiClientConfig.scheme + "://" + apiClientConfig.host + path
    }

}
