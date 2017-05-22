package com.github.pedrovgs.kuronometer.free.interpreter.formatter

import com.github.pedrovgs.kuronometer.free.interpreter.formatter.DurationFormatter.NanosecondsFormat
import com.github.pedrovgs.kuronometer.free.domain.{SummaryBuildStageExecution, SummaryBuildStagesExecution}

object SummaryBuildStageExecutionFormatter {

  private val verticalSeparator = "|"

  def format(execution: SummaryBuildStagesExecution): String = {
    val header = "-" * 21 + "\n"
    val footer = header
    val totalExecutionTime = execution.executionTimeInNanoseconds
    val stages = execution.buildStages
      .groupBy(_.name)
      .mapValues(values => values.map(_.executionTimeInNanoseconds).sum)
      .map { case (name: String, executionTime: Long) => SummaryBuildStageExecution(name, executionTime, 0) }
      .toList.sortWith { (lhs, rhs) => lhs.executionTimeInNanoseconds > rhs.executionTimeInNanoseconds}
    val chart = if (stages.nonEmpty) {
      stages
        .map { stage => format(stage, totalExecutionTime) }
        .mkString("", "\n", "\n")
    } else {
      ""
    }
    header + chart + footer
  }

  private def format(buildStage: SummaryBuildStageExecution, totalExecutionTime: Long): String = {
    val executionTime = NanosecondsFormat.format(buildStage.executionTimeInNanoseconds)
    val percentage = (buildStage.executionTimeInNanoseconds.toDouble / totalExecutionTime.toDouble) * 100
    val spaces = " " * ((100 - percentage) / 5).toInt
    val chars = "=" * (percentage / 5).toInt
    verticalSeparator + spaces + chars + verticalSeparator + " " + "%.2f".format(percentage) + " % :" + buildStage.name + ": " + executionTime
  }
}
