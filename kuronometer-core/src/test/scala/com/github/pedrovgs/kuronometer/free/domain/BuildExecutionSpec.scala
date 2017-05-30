package com.github.pedrovgs.kuronometer.free.domain

import com.github.pedrovgs.kuronometer.generators.BuildExecutionGenerators._
import com.github.pedrovgs.kuronometer.mothers.SummaryBuildStagesExecutionMother._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

class BuildExecutionSpec extends FlatSpec with Matchers with PropertyChecks {

  "BuildStagesExecution" should "returns 0 as execution time if there are no stages" in {
    BuildStagesExecution(Seq()).executionTimeInNanoseconds shouldBe 0
  }

  it should "sum the build stages execution to return the execution time" in {
    forAll { (stages: BuildStagesExecution) =>
      stages.executionTimeInNanoseconds shouldBe stages.stages
        .map(_.execution.executionTimeInNanoseconds)
        .sum
    }
  }

  "SummaryBuildStagesExecution" should "be empty if there are does not contain stages" in {
    anEmptySummary.isEmpty shouldBe true
  }

  it should "not be empty if contains stages" in {
    anyNonEmptySummary.isEmpty shouldBe false
  }

}
