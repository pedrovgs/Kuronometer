package com.github.pedrovgs.kuronometer.tasks

import com.github.pedrovgs.kuronometer.free.interpreter.Interpreters
import org.gradle.api.DefaultTask

class KuronometerTask(implicit interpreters: Interpreters)
    extends DefaultTask {
  setGroup("kuronometer")
}
