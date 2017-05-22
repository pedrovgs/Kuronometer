package com.github.pedrovgs.kuronometer

import com.github.pedrovgs.kuronometer.tasks.{GetTodayBuildTimeSummaryTask, GetTotalBuildTimeSummaryTask}
import org.gradle.api.{Plugin, Project}
import com.github.pedrovgs.kuronometer.implicits._

class KuronometerPlugin extends Plugin[Project] {

  override def apply(project: Project): Unit = {
    addTasks(project)
    addPluginExtension(project)
    addBuildListener(project)
  }

  private def addTasks(project: Project) = {
    project.getTasks.create(GetTotalBuildTimeSummaryTask.name, classOf[GetTotalBuildTimeSummaryTask])
    project.getTasks.create(GetTodayBuildTimeSummaryTask.name, classOf[GetTodayBuildTimeSummaryTask])
  }

  private def addPluginExtension(project: Project) = {
    val name = KuronometerExtension.name
    project.getExtensions.add(name, new KuronometerExtension())
  }

  private def addBuildListener(project: Project) = {
    val buildListener = KuronometerBuildListener(project)
    project.getGradle.addBuildListener(buildListener)
    project.getGradle.addListener(buildListener)
  }


}
