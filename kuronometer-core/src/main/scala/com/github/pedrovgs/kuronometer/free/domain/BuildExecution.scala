package com.github.pedrovgs.kuronometer.free.domain

object BuildTool extends Enumeration {
  val Gradle, Maven, SBT = Value
}

object Platform extends Enumeration {
  val Java, Android, Scala, Unknown = Value

  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}

case class ProjectInfo(name: String, description: Option[String])

case class ProjectVersion(group: String, version: String)

case class Project(info: ProjectInfo, version: ProjectVersion)

case class BuildStageInfo(name: String,
                          description: String,
                          group: Option[String])

case class BuildStageExecution(executionTimeInNanoseconds: Long,
                               timestamp: Long,
                               failure: Option[String],
                               skipped: Boolean)

case class BuildStage(info: BuildStageInfo, execution: BuildStageExecution)

case class BuildStagesExecution(stages: Seq[BuildStage]) {
  lazy val executionTimeInNanoseconds: Long =
    stages.map(_.execution.executionTimeInNanoseconds).sum
}

case class BuildExecution(project: Option[Project],
                          platform: Platform.Value,
                          buildTool: BuildTool.Value,
                          buildStagesExecution: BuildStagesExecution)

case class SummaryBuildStageExecution(name: String,
                                      executionTimeInNanoseconds: Long,
                                      timestamp: Long)

case class SummaryBuildStagesExecution(
    buildStages: Seq[SummaryBuildStageExecution] = Seq()) {
  def isEmpty: Boolean = buildStages.isEmpty

  lazy val executionTimeInNanoseconds: Long =
    buildStages.map(stage => stage.executionTimeInNanoseconds).sum
}
