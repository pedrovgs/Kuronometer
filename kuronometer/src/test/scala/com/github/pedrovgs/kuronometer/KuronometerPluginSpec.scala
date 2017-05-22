package com.github.pedrovgs.kuronometer

import com.github.pedrovgs.kuronometer.tasks.{GetTodayBuildTimeSummaryTask, GetTotalBuildTimeSummaryTask}
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.scalatest.{FlatSpec, Matchers}

class KuronometerPluginSpec extends FlatSpec with Matchers {

  private val project: Project = {
    val project = ProjectBuilder.builder().build()
    project.getPluginManager.apply("com.github.pedrovgs.kuronometer")
    project
  }

  "KuronometerPlugin" should "register two tasks to retrieve build execution times" in {
    project.getTasks.findByName(GetTodayBuildTimeSummaryTask.name) shouldBe a[GetTodayBuildTimeSummaryTask]
    project.getTasks.findByName(GetTotalBuildTimeSummaryTask.name) shouldBe a[GetTotalBuildTimeSummaryTask]
  }

  it should "register a kuronometer extension" in {
    project.getExtensions.findByName(KuronometerExtension.name) shouldBe a[KuronometerExtension]
  }
}
