package com.github.pedrovgs.kuronometer.generators

import com.github.pedrovgs.kuronometer.free.domain._
import org.scalacheck.{Arbitrary, Gen}

import scala.concurrent.duration._

object BuildExecutionGenerators {

  implicit lazy val arbBuildExecution: Arbitrary[BuildExecution] = Arbitrary(buildExecution())
  implicit lazy val arbBuildStagesExecution: Arbitrary[BuildStagesExecution] = Arbitrary(buildStagesExecution())
  implicit lazy val arbSummaryBuildExecution: Arbitrary[SummaryBuildStagesExecution] = Arbitrary(summaryBuildStagesExecution)

  def buildExecutionTime: Gen[Long] = Gen.choose(0, 1000.days.toNanos)

  def positive: Gen[Long] = for {
    number <- Arbitrary.arbitrary[Long]
  } yield number.abs

  def timestamp: Gen[Long] = Gen.choose(0, 1494844271152L)

  def buildStageName: Gen[String] = for {
    name <- Gen.oneOf("assemble",
      "build",
      "buildDependents",
      "classes",
      "clean",
      "jar",
      "testClasses",
      "init",
      "wrapper",
      "javadoc",
      "buildEnvironment",
      "components",
      "dependencies"
    )
  } yield name

  def summaryBuildStagesExecution: Gen[SummaryBuildStagesExecution] = for {
    stages <- Gen.listOf(summaryBuildStageExecution)
  } yield SummaryBuildStagesExecution(stages)

  def summaryBuildStageExecution: Gen[SummaryBuildStageExecution] = for {
    name <- buildStageName
    executionTime <- buildExecutionTime
    timestamp <- timestamp
  } yield SummaryBuildStageExecution(name, executionTime, timestamp)

  def buildExecution(timestamp: Gen[Long] = timestamp): Gen[BuildExecution] = for {
    project <- Gen.option(project)
    platform <- Gen.oneOf(Platform.values.toSeq)
    buildTool <- Gen.oneOf(BuildTool.values.toSeq)
    buildStagesExecution <- buildStagesExecution(timestamp)
  } yield BuildExecution(project, platform, buildTool, buildStagesExecution)

  def project: Gen[Project] = for {
    projectInfo <- projectInfo
    projectVersion <- projectVersion
  } yield Project(projectInfo, projectVersion)

  def projectInfo: Gen[ProjectInfo] = for {
    name <- Arbitrary.arbitrary[String]
    description <- Gen.option(Arbitrary.arbitrary[String])
  } yield ProjectInfo(name, description)

  def projectVersion: Gen[ProjectVersion] = for {
    group <- Arbitrary.arbitrary[String]
    version <- Arbitrary.arbitrary[String]
  } yield ProjectVersion(group, version)

  def buildStagesExecution(timestamp: Gen[Long] = timestamp): Gen[BuildStagesExecution] = for {
    stages <- Gen.listOf(buildStage(timestamp))
  } yield BuildStagesExecution(stages)

  def buildStage(timestamp: Gen[Long] = timestamp): Gen[BuildStage] = for {
    info <- buildStageInfo
    execution <- buildStageExecution(timestamp)
  } yield BuildStage(info, execution)

  def buildStageInfo: Gen[BuildStageInfo] = for {
    name <- buildStageName
    description <- Arbitrary.arbitrary[String]
    group <- Gen.option(Arbitrary.arbitrary[String])
  } yield BuildStageInfo(name, description, group)

  def buildStageExecution(timestamp: Gen[Long] = timestamp): Gen[BuildStageExecution] = for {
    executionTime <- buildExecutionTime
    timestamp <- timestamp
    failure <- Gen.option(Arbitrary.arbitrary[String])
    skipped <- Arbitrary.arbitrary[Boolean]
  } yield BuildStageExecution(executionTime, timestamp, failure, skipped)
}
