package com.github.pedrovgs.kuronometer

import com.github.pedrovgs.kuronometer.KuronometerResults.KuronometerError
import com.github.pedrovgs.kuronometer.KuronometerSpecImplicits._
import com.github.pedrovgs.kuronometer.free.domain.{BuildExecution, Config, SummaryBuildStagesExecution}
import com.github.pedrovgs.kuronometer.free.interpreter.api.KuronometerApiClientConfig
import com.github.pedrovgs.kuronometer.generators.BuildExecutionGenerators._
import com.github.pedrovgs.kuronometer.generators.ConfigGenerators.config
import com.github.pedrovgs.kuronometer.generators.KuronometerErrorGenerators._
import com.github.pedrovgs.kuronometer.mothers.ConfigMother
import org.scalacheck.Gen
import org.scalamock.scalatest.MockFactory
import org.scalatest._
import org.scalatest.prop.PropertyChecks

class KuronometerSpec extends FlatSpec with Matchers with PropertyChecks with MockFactory {

  private val apiClientConfig = KuronometerApiClientConfig()

  "Kuronometer" should "point to production" in {
    apiClientConfig.scheme shouldBe "https"
    apiClientConfig.host shouldBe "kuronometer.io"
    apiClientConfig.port shouldBe 80
  }

  it should "returns the build report as a success even when the remote reporter fails due to any error" in {
    forAll { (build: BuildExecution, error: KuronometerError) =>
      (apiClient.report _).expects(build).returning(Left(error))
      (csvReporter.report _).expects(build).returning(Right(build))

      Kuronometer.reportBuildFinished(build, ConfigMother.anyConfig) shouldBe Right(build)
    }
  }

  it should "returns the build report as an error if the local reporter fails due to any error" in {
    forAll { (build: BuildExecution, error: KuronometerError) =>
      (apiClient.report _).expects(build).returning(Right(build))
      (csvReporter.report _).expects(build).returning(Left(error))

      Kuronometer.reportBuildFinished(build, ConfigMother.anyConfig) shouldBe (Left(error))
    }
  }

  it should "always report two the local and remote reporters if the config reports data remotely" in {
    forAll(buildExecution(), config(Gen.const(true))) { (build: BuildExecution, config: Config) =>
      (apiClient.report _).expects(*).returning(Right(build))
      (csvReporter.report _).expects(*).returning(Right(build))

      Kuronometer.reportBuildFinished(build, config).isRight shouldBe true
    }
  }

  it should "just report to the local reporter if the config does not report data remotely" in {
    forAll(buildExecution(), config(Gen.const(false))) { (build: BuildExecution, config: Config) =>
      (apiClient.report _).expects(*).never().returning(Right(build))
      (csvReporter.report _).expects(*).returning(Right(build))

      Kuronometer.reportBuildFinished(build, config).isRight shouldBe true
    }
  }

  it should "anonymize the build execution if the config does not report project info" in {
    forAll { (build: BuildExecution) =>
      val anonymousBuild = build.copy(project = None)
      (apiClient.report _).expects(anonymousBuild).returning(Right(anonymousBuild))
      (csvReporter.report _).expects(anonymousBuild).returning(Right(anonymousBuild))

      Kuronometer.reportBuildFinished(build, ConfigMother.anyAnonymousConfig) shouldBe Right(anonymousBuild)
    }
  }

  it should "return the total build execution time obtained from the CSV reporter" in {
    forAll { (summary: SummaryBuildStagesExecution) =>
      (csvReporter.getTotalBuildExecutionStages _).expects().returning(Right(summary))

      Kuronometer.getTotalBuildExecutionSummary shouldBe Right(summary)
    }
  }

  it should "return the error generated by the CSV reporter getting the total build execution time" in {
    forAll { (error: KuronometerError) =>
      (csvReporter.getTotalBuildExecutionStages _).expects().returning(Left(error))

      Kuronometer.getTotalBuildExecutionSummary shouldBe Left(error)
    }
  }

  it should "return the today build execution time obtained from the CSV reporter" in {
    forAll { (summary: SummaryBuildStagesExecution) =>
      val maxTimestamp = if (summary.isEmpty) 0 else {
        summary.buildStages.map(_.executionTimeInNanoseconds).max
      }
      (clock.todayMidnightTimestamp _).expects().returning(maxTimestamp)
      (csvReporter.getBuildExecutionStagesSinceTimestamp _).expects(*).returning(Right(summary))

      Kuronometer.getTodayBuildExecutionSummary shouldBe Right(summary)
    }
  }

  it should "return the error generated by the CSV reporter getting the today build execution time" in {
    forAll { (error: KuronometerError) =>
      (csvReporter.getTotalBuildExecutionStages _).expects().returning(Left(error))

      Kuronometer.getTotalBuildExecutionSummary shouldBe Left(error)
    }
  }

  it should "use the midnight timestamp to filter today build execution times" in {
    forAll(summaryBuildStagesExecution, timestamp) { (summary: SummaryBuildStagesExecution, timestamp: Long) =>
      (clock.todayMidnightTimestamp _).expects().returning(timestamp)
      (csvReporter.getBuildExecutionStagesSinceTimestamp _).expects(timestamp).returning(Right(summary))

      Kuronometer.getTodayBuildExecutionSummary shouldBe Right(summary)
    }
  }
}
