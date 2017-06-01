package com.github.pedrovgs.kuronometer.free.interpreter.api

import com.github.pedrovgs.kuronometer.KuronometerResults.{
  KuronometerResult,
  UnknownError
}
import com.github.pedrovgs.kuronometer.free.domain.BuildExecution
import com.github.pedrovgs.kuronometer.mothers.BuildExecutionMother
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Future

class KuronometerApiClientSpec
    extends FlatSpec
    with Matchers
    with StubbingHttp
    with Resources
    with ScalaFutures {

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(1, Seconds), interval = Span(100, Millis))

  private val reportBuildExecutionPath = "/buildExecution"
  private implicit def apiClientConfig =
    KuronometerApiClientConfig(scheme, host, port)
  private val apiClient = new KuronometerApiClient()

  "KuronometerApiClient" should "report a build execution to the correct path using a post request" in {
    givenTheBuildExecutionIsReportedProperly()

    whenReady(report()) { _ =>
      verify(postRequestedFor(urlEqualTo(reportBuildExecutionPath)))
    }
  }

  it should "return the build execution reported on success" in {
    givenTheBuildExecutionIsReportedProperly()

    val buildExecution = BuildExecutionMother.anyBuildExecution
    whenReady(report(buildExecution)) { result =>
      result shouldBe Right(buildExecution)
    }
  }

  it should "return an UnknownError if something goes wrong in server side" in {
    givenTheBuildExecutionReportFails()

    whenReady(report()) { result =>
      result shouldBe Left(UnknownError())
    }
  }

  it should "send the build execution as part of the report request serialized into json" in {
    givenTheBuildExecutionIsReportedProperly()

    whenReady(report()) { _ =>
      verify(
        postRequestedFor(urlEqualTo(reportBuildExecutionPath))
          .withRequestBody(
            equalToJson(fileContent("/reportBuildExecutionRequest.json"))))
    }
  }

  it should "send the build execution anonymously as part of the report request serialized into json" in {
    givenTheBuildExecutionIsReportedProperly()

    whenReady(report(BuildExecutionMother.anonymousBuildExecution)) { _ =>
      verify(
        postRequestedFor(urlEqualTo(reportBuildExecutionPath))
          .withRequestBody(equalToJson(
            fileContent("/reportAnonymousBuildExecutionRequest.json"))))
    }
  }

  it should "send the accept application json header as part of the request" in {
    givenTheBuildExecutionIsReportedProperly()

    whenReady(report()) { _ =>
      verify(
        postRequestedFor(urlEqualTo(reportBuildExecutionPath))
          .withHeader("Content-Type",
                      equalTo("application/json; charset=UTF-8")))
    }
  }

  private def report(
      buildExecution: BuildExecution = BuildExecutionMother.anyBuildExecution)
    : Future[KuronometerResult[BuildExecution]] = {
    apiClient.report(buildExecution)
  }

  private def givenTheBuildExecutionIsReportedProperly() = {
    givenTheBuildExecutionReturns(201)
  }

  private def givenTheBuildExecutionReportFails() = {
    givenTheBuildExecutionReturns(500)
  }

  private def givenTheBuildExecutionReturns(statusCode: Int): Unit = {
    stubFor(
      post(urlEqualTo("/buildExecution"))
        .willReturn(aResponse()
          .withStatus(statusCode)))
  }
}
