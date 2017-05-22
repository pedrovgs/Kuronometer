package com.github.pedrovgs.kuronometer

import org.scalatest.{FlatSpec, Matchers}

class KuronometerExtensionSpec extends FlatSpec with Matchers {

  private val extension = new KuronometerExtension()

  "KuronometerExtension" should "use Java as the default platform" in {
    extension.getPlatformName shouldBe "Java"
  }

  it should "report project info by default" in {
    extension.getReportProjectInfo shouldBe true
  }

  it should "report data remotely by default" in {
    extension.getReportDataRemotely shouldBe true
  }

  it should "not use verbose mode by default" in {
    extension.getVerbose shouldBe false
  }

}
