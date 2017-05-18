package com.github.pedrovgs.kuronometer.free.interpreter

import org.joda.time.DateTime

class Clock {

  def todayMidnightTimestamp: Long = {
    new DateTime().withTimeAtStartOfDay().getMillis
  }

}
