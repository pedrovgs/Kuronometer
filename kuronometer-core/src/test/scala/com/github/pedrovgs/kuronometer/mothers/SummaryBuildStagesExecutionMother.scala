package com.github.pedrovgs.kuronometer.mothers

import com.github.pedrovgs.kuronometer.free.domain.{SummaryBuildStageExecution, SummaryBuildStagesExecution}

object SummaryBuildStagesExecutionMother {

  val anEmptySummary: SummaryBuildStagesExecution = SummaryBuildStagesExecution()

  val anyNonEmptySummary: SummaryBuildStagesExecution = {
    val stage1 = SummaryBuildStageExecution("compile", anyExecutionTime, anyExecutionTimestamp)
    val stage2 = SummaryBuildStageExecution("test", anyExecutionTime, anyExecutionTimestamp)
    SummaryBuildStagesExecution(Seq(stage1, stage2))
  }

  val anyExecutionTimestamp: Long = 123456789L

  val anyExecutionTime: Long = 2000000

}
