package com.github.pedrovgs.kuronometer.free.interpreter.formatter

import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

object DurationFormatter {

  object MillisecondsFormat
      extends TimeUnitFormat(TimeUnitAndQuantity.MillisecondToDay)

  object NanosecondsFormat
      extends TimeUnitFormat(TimeUnitAndQuantity.NanosecondToDay)

  private case class FormattedPartsAndRemainingValue(
      formattedParts: List[Option[String]],
      remainingValue: Long) {
    private def divMod(numerator: Long, denominator: Long): (Long, Long) =
      (numerator / denominator, numerator % denominator)

    def applyTimeUnit(timeUnitAndQuantity: TimeUnitAndQuantity)
      : FormattedPartsAndRemainingValue = {
      timeUnitAndQuantity match {
        case TimeUnitAndQuantity(timeUnit, Some(quantity)) =>
          val (newRemainingValue, partOfValueToFormat) =
            divMod(remainingValue, quantity)
          val formattedPart = timeUnit.format(partOfValueToFormat)
          copy(formattedPart :: formattedParts, newRemainingValue)
        case TimeUnitAndQuantity(timeUnit, None) =>
          val formattedPart = timeUnit.format(remainingValue)
          copy(formattedPart :: formattedParts, remainingValue)
      }
    }
  }

  private case class QuantityAndName(quantity: Long, timeUnit: TimeUnit) {
    def toUnitsAtScale(fullScale: List[TimeUnitAndQuantity]): Long = {
      val scale = fullScale.takeWhile(_.timeUnit != timeUnit)

      def accumulateByMultiply(
          soFar: Long,
          timeUnitAndQuantity: TimeUnitAndQuantity): Long = {
        timeUnitAndQuantity.maybeQuantity match {
          case Some(currentQuantity) => soFar * currentQuantity
          case None =>
            throw new RuntimeException(
              s"No multiplier for ${timeUnitAndQuantity.timeUnit.plural}")
        }
      }

      val units = scale.foldLeft(quantity)(accumulateByMultiply)
      units
    }
  }

  sealed abstract case class TimeUnit(singular: String, plural: String) {
    TimeUnit.valuesBuffer += this

    def format(value: Long): Option[String] =
      if (value == 0) None
      else if (value == 1) Some(s"$value $singular")
      else Some(s"$value $plural")

    def matchesString(target: String): Boolean = {
      singular.equalsIgnoreCase(target) || plural.equalsIgnoreCase(target)
    }
  }

  private object TimeUnit {
    private val valuesBuffer = new ArrayBuffer[TimeUnit]
    lazy val values = valuesBuffer.toSeq

    val Nanosecond = new TimeUnit("ns", "ns") {}
    val Microsecond = new TimeUnit("μs", "μs") {}
    val Millisecond = new TimeUnit("ms", "ms") {}
    val Second = new TimeUnit("sec", "secs") {}
    val Minute = new TimeUnit("min", "mins") {}
    val Hour = new TimeUnit("hour", "hours") {}
    val Day = new TimeUnit("day", "days") {}
  }

  private case class TimeUnitAndQuantity(timeUnit: TimeUnit,
                                         maybeQuantity: Option[Int])

  private object TimeUnitAndQuantity {
    val MillisecondToDay =
      TimeUnitAndQuantity(TimeUnit.Millisecond, Some(1000)) ::
        TimeUnitAndQuantity(TimeUnit.Second, Some(60)) ::
        TimeUnitAndQuantity(TimeUnit.Minute, Some(60)) ::
        TimeUnitAndQuantity(TimeUnit.Hour, Some(24)) ::
        TimeUnitAndQuantity(TimeUnit.Day, None) ::
        Nil
    val NanosecondToDay =
      TimeUnitAndQuantity(TimeUnit.Nanosecond, Some(1000)) ::
        TimeUnitAndQuantity(TimeUnit.Microsecond, Some(1000)) ::
        MillisecondToDay
  }

  class TimeUnitFormat(scale: List[TimeUnitAndQuantity]) {

    import TimeUnitFormat._

    def format(smallestUnits: Long): String = {
      def accumulateFormat(soFar: FormattedPartsAndRemainingValue,
                           timeUnitAndQuantity: TimeUnitAndQuantity)
        : FormattedPartsAndRemainingValue = {
        soFar.applyTimeUnit(timeUnitAndQuantity)
      }

      val initialValue = FormattedPartsAndRemainingValue(Nil, smallestUnits)
      val finalValue = scale.foldLeft(initialValue)(accumulateFormat)
      val formattedParts = finalValue.formattedParts.flatten
      if (formattedParts.isEmpty) "0 " + scale.head.timeUnit.plural
      else formattedParts.mkString(" ")
    }

    def parse(asString: String): Long = {
      if (asString.matches(NumberPattern)) {
        parseSimpleNumber(asString)
      } else if (asString.matches(OneOrMoreQuantifiedTimeUnitPattern)) {
        parseStringWithUnits(asString)
      } else {
        throw new RuntimeException(
          s"'$asString' does not match a valid pattern: $OneOrMoreQuantifiedTimeUnitPattern")
      }
    }

    private def parseSimpleNumber(asString: String): Long = {
      asString.toLong
    }

    private def parseStringWithUnits(asString: String): Long = {
      val parts = for {
        matchData <- QuantifiedTimeUnitCapturingRegex
          .findAllIn(asString)
          .matchData
        numberString = matchData.group("number")
        nameString = matchData.group("name")
      } yield {
        val name = timeUnitFromString(nameString)
        val number = numberString.toLong
        val quantityAndName = QuantityAndName(number, name)
        quantityAndName.toUnitsAtScale(scale)
      }
      val sum = parts.sum
      sum
    }

    def timeUnitFromString(asString: String): TimeUnit = {
      val pluralNames = scale.map(_.timeUnit.plural).mkString("(", ", ", ")")

      def timeUnitMatches(timeUnit: TimeUnit): Boolean =
        timeUnit.matchesString(asString)

      TimeUnit.values.find(timeUnitMatches) match {
        case Some(timeUnit) => timeUnit
        case None =>
          throw new RuntimeException(
            s"'$asString' does not match a valid time unit $pluralNames")
      }
    }
  }

  private object TimeUnitFormat {
    private val NumberPattern = """\d+"""
    private val NamePattern = """[a-zA-Zμ]+"""
    private val SpacesPattern = """\s+"""
    private val QuantifiedTimeUnitPattern = NumberPattern + SpacesPattern + NamePattern
    private val QuantifiedTimeUnitCapturingPattern = capturingGroup(
      NumberPattern) + SpacesPattern + capturingGroup(NamePattern)
    private val OneOrMoreQuantifiedTimeUnitPattern = QuantifiedTimeUnitPattern + nonCapturingGroup(
      SpacesPattern + QuantifiedTimeUnitPattern) + "*"
    private val QuantifiedTimeUnitCapturingRegex =
      new Regex(QuantifiedTimeUnitCapturingPattern, "number", "name")

    private def nonCapturingGroup(s: String) = "(?:" + s + ")"

    private def capturingGroup(s: String) = "(" + s + ")"
  }

}
