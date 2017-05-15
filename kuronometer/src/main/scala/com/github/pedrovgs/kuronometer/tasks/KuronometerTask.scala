package com.github.pedrovgs.kuronometer.tasks

import com.github.pedrovgs.kuronometer.Kuronometer
import com.github.pedrovgs.kuronometer.implicits._
import org.gradle.api.DefaultTask

class KuronometerTask(implicit val kuronometer: Kuronometer = new Kuronometer()) extends DefaultTask {
  setGroup("kuronometer")
}
