package com.github.pedrovgs.kuronometer.mothers

import com.github.pedrovgs.kuronometer.free.domain._

object BuildExecutionMother {

  val anyProjectInfo: ProjectInfo = {
    ProjectInfo(
      "kuronometer",
      Some(
        "Let's measure how long developers around the world are compiling software."))
  }

  val anyVersion: ProjectVersion = {
    ProjectVersion("com.github.pedrovgs", "0.0.1")
  }

  val anyProject: Project = {
    val info = anyProjectInfo
    val version = anyVersion
    Project(info, version)
  }

  val anyBuildStages: Seq[BuildStage] = {
    val buildStageInfo1 =
      BuildStageInfo("test", "Runs the unit tests.", Some("verification"))
    val buildStageExecution1 =
      BuildStageExecution(6086138, 1493679320207L, None, skipped = false)
    val buildStageInfo2 =
      BuildStageInfo("compileJava", "Compiles main Java source.", None)
    val buildStageExecution2 =
      BuildStageExecution(4010896, 1493679320162L, None, skipped = true)
    val buildStage1 = BuildStage(buildStageInfo1, buildStageExecution1)
    val buildStage2 = BuildStage(buildStageInfo2, buildStageExecution2)
    List(buildStage1, buildStage2)
  }

  val anyBuildExecution: BuildExecution = {
    val stages = anyBuildStages
    val project = anyProject
    val buildStagesExecution = BuildStagesExecution(stages)
    BuildExecution(Some(project),
                   Platform.Java,
                   BuildTool.Gradle,
                   buildStagesExecution)
  }

  val anonymousBuildExecution: BuildExecution = {
    val stages = anyBuildStages
    val buildStagesExecution = BuildStagesExecution(stages)
    BuildExecution(None, Platform.Java, BuildTool.Gradle, buildStagesExecution)
  }

}
