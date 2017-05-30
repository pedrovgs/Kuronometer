package com.github.pedrovgs.kuronometer.mothers

import com.github.pedrovgs.kuronometer.free.domain.{Config, Platform}

object ConfigMother {

  val anyConfig: Config = Config(Platform.Java,
                                 reportProjectInfo = true,
                                 reportDataRemotely = true,
                                 verbose = false)

  val anyAnonymousConfig: Config = Config(Platform.Java,
                                          reportProjectInfo = false,
                                          reportDataRemotely = true,
                                          verbose = false)
}
