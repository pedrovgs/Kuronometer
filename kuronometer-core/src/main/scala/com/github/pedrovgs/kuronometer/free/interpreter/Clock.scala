package com.github.pedrovgs.kuronometer.free.interpreter

import java.util.{Calendar, GregorianCalendar}

class Clock {

  def todayMidnightTimestamp: Long = {
    val date: Calendar = new GregorianCalendar()
    date.set(Calendar.HOUR_OF_DAY, 0)
    date.set(Calendar.MINUTE, 0)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
    date.getTimeInMillis
  }

}
