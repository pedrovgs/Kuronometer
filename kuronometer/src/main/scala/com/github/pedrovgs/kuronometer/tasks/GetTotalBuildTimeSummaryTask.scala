package com.github.pedrovgs.kuronometer.tasks

import com.github.pedrovgs.kuronometer.Kuronometer
import com.github.pedrovgs.kuronometer.app.KuronometerProgram
import com.github.pedrovgs.kuronometer.implicits._
import org.gradle.api.tasks.TaskAction

object GetTotalBuildTimeSummaryTask {
  val name = "totalBuildTime"
}

class GetTotalBuildTimeSummaryTask() extends KuronometerTask {

  setDescription(
    "Displays the project total build time and a report of the different tasks execution times.")

  @TaskAction
  def totalBuildTime(): Unit = {
    Kuronometer
      .getTotalBuildExecutionSummary[KuronometerProgram]
      .foldMap(interpreters.kuronometerInterpreter)
  }
}
