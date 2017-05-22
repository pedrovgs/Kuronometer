package com.github.pedrovgs.kuronometer.free.interpreter.api

import scala.io.Source

trait Resources {

  def fileContent(path: String): String = {
    val resource = getClass.getResource(path)
    val file = Source.fromURL(resource)
    file.getLines().mkString
  }
}
