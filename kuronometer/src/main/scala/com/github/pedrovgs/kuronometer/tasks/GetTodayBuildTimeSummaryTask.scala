package com.github.pedrovgs.kuronometer.tasks

import com.github.pedrovgs.kuronometer.Kuronometer
import org.gradle.api.tasks.TaskAction
import com.github.pedrovgs.kuronometer.implicits._

object GetTodayBuildTimeSummaryTask {
  val name = "todayBuildTime"
}

class GetTodayBuildTimeSummaryTask() extends KuronometerTask {

  setDescription("Displays the project today build time and a report of the different tasks execution times.")

  @TaskAction
  def todayBuildTime(): Unit = {
    Kuronometer.getTodayBuildExecutionSummary
  }
}
