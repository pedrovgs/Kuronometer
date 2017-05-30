package com.github.pedrovgs.kuronometer.free.interpreter.api

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import org.scalatest.{BeforeAndAfterEach, Suite}

trait StubbingHttp extends BeforeAndAfterEach { this: Suite =>

  val port = 8080
  val host = "localhost"
  val scheme = "http"
  val wireMockServer = new WireMockServer(wireMockConfig().port(port))

  abstract override def beforeEach {
    wireMockServer.start()
    WireMock.configureFor(host, port)
  }

  abstract override def afterEach {
    wireMockServer.resetAll()
    wireMockServer.stop()
  }
}
