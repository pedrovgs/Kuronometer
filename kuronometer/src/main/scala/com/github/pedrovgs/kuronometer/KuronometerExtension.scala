package com.github.pedrovgs.kuronometer

import scala.beans.BeanProperty

object KuronometerExtension {
  val name = "kuronometer"
}

class KuronometerExtension(@BeanProperty var platformName: String,
                           @BeanProperty var reportProjectInfo: Boolean,
                           @BeanProperty var reportDataRemotely: Boolean,
                           @BeanProperty var verbose: Boolean) {

  //This default empty constructor is needed by the Gradle plugins API.
  def this() = this("Java", true, true, false)

}
