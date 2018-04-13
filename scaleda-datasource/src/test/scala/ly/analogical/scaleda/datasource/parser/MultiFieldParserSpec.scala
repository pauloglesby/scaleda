package ly.analogical.scaleda
package datasource
package parser

import cats.syntax.either._
import shapeless._
import org.scalacheck.{Gen, Shrink}
import org.scalatest.EitherValues
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.util.Try

class MultiFieldParserSpec extends BaseSpec with GeneratorDrivenPropertyChecks with EitherValues {

  import MultiFieldParser._

  private val maxFields = 10
  private val fieldCountGenerator: Gen[Int] = Gen.choose(1, maxFields)
  implicit val noShrink: Shrink[Int] = Shrink.shrinkAny

  def parseSeqAsString[A]: Seq[A] => String = _.mkString(",")

  implicit class RichInt(i: Int) {
    def toSequenceOfStrings: Seq[Field] = (1 to i).map(_.toString)
  }

  private val singleFieldInput = 1.toSequenceOfStrings

  "MultiFieldParser" when {

    "calling the `instance` method" should {

      "fail with `Error.IncorrectFieldCount` if the input size is too small" in {
        forAll(fieldCountGenerator) { fieldCount =>
          val oneOff = fieldCount - 1
          val input = oneOff.toSequenceOfStrings
          val parser = instance[String](fieldCount)(parseSeqAsString(_).asRight[Error])
          parser.parse(input).left.value should equal(Error.IncorrectFieldCount(oneOff, fieldCount, input))
        }
      }

      "fail with `Error.IncorrectFieldCount` if the input size is too large" in {
        forAll(fieldCountGenerator) { fieldCount =>
          val oneOff = fieldCount + 1
          val input = oneOff.toSequenceOfStrings
          val parser = instance[String](fieldCount)(parseSeqAsString(_).asRight[Error])
          parser.parse(input).left.value should equal(Error.IncorrectFieldCount(oneOff, fieldCount, input))
        }
      }

      "return the parse result from the provided function if the input size is correct" in {
        forAll(fieldCountGenerator) { fieldCount =>
          val input = fieldCount.toSequenceOfStrings
          val parser = instance[String](fieldCount)(parseSeqAsString(_).asRight[Error])
          parser.parse(input).right.value should equal(input.mkString(","))
        }
      }

    }

    "calling the `headInstance` method" should {

      "fail with `Error.IncorrectFieldCount` if the input size is greater than 1" in {
        forAll(fieldCountGenerator suchThat(_ > 1)) { fieldCount =>
          val input = fieldCount.toSequenceOfStrings
          val parser = headInstance[String](_.toString.asRight[Error])
          parser.parse(input).left.value should equal(Error.IncorrectFieldCount(fieldCount, 1, input))
        }
      }

      "return the parse result from the provided function if the input size is 1" in {
        val parser = headInstance[String](_.toString.asRight[Error])
        parser.parse(singleFieldInput).right.value should equal("1")
      }

    }

    "calling `parse` on well-formed parser" should {

      "return `Left(Error.NotParsable)` if applying the parse function to the input returns Error.NotParsable" in {
        val parser = headInstance[String](Error.NotParsable(_, "blah").asLeft[String])
        parser.parse(singleFieldInput).left.value should equal(Error.NotParsable("1", "blah"))
      }

      "return `Left(Error.NotParsableAs)` if applying the parse function to the input returns Error.NotParsableAs" in {
        val parser = headInstance[Int](Error.NotParsableAs[Int](_).asLeft[Int])
        parser.parse(List("1.05")).left.value should equal(Error.NotParsableAs[Int]("1.05"))
      }

    }

    "implicitly defined typeclasses exist they" should {

      "be able to define a hlist parser if every element can be parsed" in {
        implicit val intParser = headInstance[Int] { field =>
          Either.fromTry(Try(field.toInt)).leftMap(t => Error.NotParsable(field, t.getMessage))
        }

        implicit val doubleParser = headInstance[Double] { field =>
          Either.fromTry(Try(field.toDouble)).leftMap(t => Error.NotParsable(field, t.getMessage))
        }

        type TestList = Int :: Double :: HNil

        val hlistParser: MultiFieldParser[TestList] = MultiFieldParser[TestList]
        hlistParser.parse(List("1", "1.05")).right.value should equal(1 :: 1.05D :: HNil)
      }

    }

    "operating on an interim result of `Either[Throwable, A].left` should convert to left `Result` of `Error.NotParsable`" in {
      val throwable = new Throwable("blah")
      throwable.asLeft.toMultiFieldParserResult("field").left.value should equal(Error.NotParsable("field", "blah"))
    }

    "operating on an interim result of `Either[Throwable, A].right` should convert to right `Result` of `A`" in {
      "yay!".asRight[Throwable].toMultiFieldParserResult("yay!").right.value should equal("yay!")
    }

  }

}
