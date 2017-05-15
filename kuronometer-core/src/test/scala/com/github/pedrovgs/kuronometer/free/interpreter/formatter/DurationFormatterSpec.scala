package com.github.pedrovgs.kuronometer.free.interpreter.formatter

import com.github.pedrovgs.kuronometer.generators.BuildExecutionGenerators._
import org.scalatest.prop.PropertyChecks
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

class DurationFormatterSpec extends FlatSpec with Matchers with PropertyChecks {

  private val formatter = DurationFormatter.NanosecondsFormat

  "DurationFormatter" should "show 0 nanoseconds as 0 ns" in {
    formatter.format(0) shouldBe "0 ns"
  }

  it should "show 1 ns as 1 ns" in {
    formatter.format(1) shouldBe "1 ns"
  }

  it should "show 1000 ns as 1 μs" in {
    formatter.format(1000) shouldBe "1 μs"
  }

  it should "show values greater than 1000 ns as the composition of μs plus ns" in {
    formatter.format(1001) shouldBe "1 μs 1 ns"
  }

  it should "show values greater than 10000000 ns as the composition of ms plus μs plus ns" in {
    formatter.format(1000001) shouldBe "1 ms 1 ns"
  }

  it should "show values composed by ms μs and ns" in {
    formatter.format(1001001) shouldBe "1 ms 1 μs 1 ns"
  }

  it should "show values composed by secs ms μs and ns" in {
    formatter.format(2001001001) shouldBe "2 secs 1 ms 1 μs 1 ns"
  }

  it should "show values composed by minutes secs ms μs and ns" in {
    formatter.format(62001001001L) shouldBe "1 min 2 secs 1 ms 1 μs 1 ns"
  }

  it should "show values composed by hours minutes secs ms μs and ns" in {
    formatter.format(3662001001001L) shouldBe "1 hour 1 min 2 secs 1 ms 1 μs 1 ns"
  }

  it should "shows 1 day" in {
    val duration: Duration = 1.days
    formatter.format(duration.toNanos) shouldBe "1 day"
  }

  it should "show more than 1 day in plural" in {
    val duration: Duration = 2.days
    formatter.format(duration.toNanos) shouldBe "2 days"
  }

  it should "keep symmetry property" in {
    forAll(buildExecutionTime) { (executionTime: Long) =>
      val formattedString = formatter.format(executionTime)
      formatter.parse(formattedString) shouldBe executionTime
    }
  }
}
