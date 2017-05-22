package com.github.pedrovgs.kuronometer.free.interpreter.formatter

import com.github.pedrovgs.kuronometer.free.domain.{SummaryBuildStageExecution, SummaryBuildStagesExecution}
import com.github.pedrovgs.kuronometer.generators.BuildExecutionGenerators._
import com.github.pedrovgs.kuronometer.mothers.SummaryBuildStagesExecutionMother._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

class SummaryBuildStageExecutionFormatterSpec extends FlatSpec with Matchers with PropertyChecks {

  private val formatter = SummaryBuildStageExecutionFormatter

  "SummaryBuildStageExecutionFormatterSpec" should "show an empty report if the summary is empty" in {
    formatter.format(anEmptySummary) shouldBe
      "---------------------" + "\n" +
        "---------------------" + "\n"
  }

  it should "show the content of the summary build execution and the associated percentage" in {
    val stage1 = SummaryBuildStageExecution("compile", 3.seconds.toNanos, anyExecutionTimestamp)
    val stage2 = SummaryBuildStageExecution("test", 1.seconds.toNanos, anyExecutionTimestamp)
    val summary = SummaryBuildStagesExecution(Seq(stage1, stage2))
    formatter.format(summary) shouldBe
      "---------------------\n" +
        "|     ===============| 75.00 % :compile: 3 secs\n" +
        "|               =====| 25.00 % :test: 1 sec\n" +
        "---------------------\n"
  }

  it should "show the content of the summary build execution using two decimals when needed" in {
    val stage1 = SummaryBuildStageExecution("compile", 2.seconds.toNanos, anyExecutionTimestamp)
    val stage2 = SummaryBuildStageExecution("test", 1.seconds.toNanos, anyExecutionTimestamp)
    val summary = SummaryBuildStagesExecution(Seq(stage1, stage2))
    formatter.format(summary) shouldBe
      "---------------------\n" +
        "|      =============| 66.67 % :compile: 2 secs\n" +
        "|             ======| 33.33 % :test: 1 sec" +
        "\n---------------------\n"
  }

  it should "combine stages with the same name" in {
    val stage1 = SummaryBuildStageExecution("compile", 2.seconds.toNanos, anyExecutionTimestamp)
    val stage2 = SummaryBuildStageExecution("test", 1.seconds.toNanos, anyExecutionTimestamp)
    val stage3 = SummaryBuildStageExecution("compile", 1.seconds.toNanos, anyExecutionTimestamp)
    val summary = SummaryBuildStagesExecution(Seq(stage1, stage2, stage3))
    formatter.format(summary) shouldBe
      "---------------------\n" +
        "|     ===============| 75.00 % :compile: 3 secs\n" +
        "|               =====| 25.00 % :test: 1 sec\n" +
        "---------------------\n"
  }

  it should "show every build stage execution name in the formatted report" in {
    forAll { (summary: SummaryBuildStagesExecution) =>
      val formattedSummary = formatter.format(summary)
      summary.buildStages.foreach { stage =>
        formattedSummary.contains(stage.name)
      }
    }
  }

}
