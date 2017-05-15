package com.github.pedrovgs.kuronometer.generators

import com.github.pedrovgs.kuronometer.free.domain.{Config, Platform}
import org.scalacheck.{Arbitrary, Gen}

object ConfigGenerators {

  implicit lazy val arbConfig: Arbitrary[Config] = Arbitrary(config())

  def config(reportDataRemotely: Gen[Boolean] = Arbitrary.arbitrary[Boolean]): Gen[Config] = for {
    platform <- Gen.oneOf(Platform.values.toSeq)
    reportProjectInfo <- Arbitrary.arbitrary[Boolean]
    reportDataRemotely <- reportDataRemotely
    verbose <- Arbitrary.arbitrary[Boolean]
  } yield Config(platform, reportProjectInfo, reportDataRemotely, verbose)

}
