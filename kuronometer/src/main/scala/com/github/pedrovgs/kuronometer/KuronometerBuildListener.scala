package com.github.pedrovgs.kuronometer

import com.github.pedrovgs.kuronometer.app.KuronometerProgram
import com.github.pedrovgs.kuronometer.free.domain._
import com.github.pedrovgs.kuronometer.free.interpreter.Interpreters
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState
import org.gradle.api.{Project, Task}
import org.gradle.{BuildListener, BuildResult}

case class KuronometerBuildListener(project: Project)(implicit interpreters: Interpreters) extends BuildListener with TaskExecutionListener {

  private var buildStages = List[BuildStage]()
  private var taskStartTimestamp: Long = 0

  private lazy val config: Config = {
    val extension = project.getExtensions.getByType(new KuronometerExtension().getClass)
    val platform = Platform.withNameOpt(extension.getPlatformName).getOrElse(Platform.Java)
    val reportProjectInfo = extension.getReportProjectInfo
    val reportDataRemotely = extension.getReportDataRemotely
    val verbose = extension.getVerbose
    Config(platform, reportProjectInfo = reportProjectInfo, reportDataRemotely = reportDataRemotely, verbose = verbose)
  }

  override def settingsEvaluated(var1: Settings): Unit = {}

  override def projectsLoaded(var1: Gradle): Unit = {}

  override def projectsEvaluated(var1: Gradle): Unit = {}

  override def buildStarted(gradle: Gradle): Unit = {}

  override def beforeExecute(task: Task): Unit = {
    taskStartTimestamp = System.nanoTime()
  }

  override def afterExecute(task: Task, taskState: TaskState): Unit = {
    val executionTimeInNanoseconds = System.nanoTime() - taskStartTimestamp
    buildStages = buildStages :+ mapTaskToBuildStage(task, taskState, executionTimeInNanoseconds)
  }

  override def buildFinished(buildResult: BuildResult): Unit = {
    val kuronometerProject = mapGradleProjectToKuronometerProject(project)
    val buildStagesExecution = BuildStagesExecution(buildStages)
    val buildExecution = BuildExecution(Some(kuronometerProject), config.platform, BuildTool.Gradle, buildStagesExecution)
    Kuronometer.reportBuildFinished[KuronometerProgram](buildExecution, config).foldMap(interpreters.kuronometerInterpreter)
  }

  private def mapGradleProjectToKuronometerProject(project: Project): com.github.pedrovgs.kuronometer.free.domain.Project = {
    val KuronometerProject = com.github.pedrovgs.kuronometer.free.domain.Project
    val projectInfo = ProjectInfo(project.getName, Option(project.getDescription))
    val projectVersion = ProjectVersion(project.getGroup.toString, project.getVersion.toString)
    KuronometerProject(projectInfo, projectVersion)
  }

  private def mapTaskToBuildStage(task: Task, taskState: TaskState, executionTimeInNanoseconds: Long): BuildStage = {
    val buildStageInfo = BuildStageInfo(task.getName, task.getDescription, Option(task.getGroup))
    val failure = Option(taskState.getFailure)
    val failureMessage = failure.map(_.toString)
    val buildStageExecution = BuildStageExecution(executionTimeInNanoseconds, System.currentTimeMillis(), failureMessage, taskState.getSkipped)
    BuildStage(buildStageInfo, buildStageExecution)
  }

}
