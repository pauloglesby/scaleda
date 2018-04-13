package ly.analogical.scaleda
package datasource
package parser

import cats.syntax.either._
import shapeless.{::, HList, HNil, Lazy}

import scala.reflect.{ClassTag, classTag}

trait MultiFieldParser[A] {
  def parse(fields: Seq[MultiFieldParser.Field]): MultiFieldParser.Result[A]
  def fieldCount: Int
}

object MultiFieldParser {

  type Field = String
  type Result[A] = Either[Error, A]

  sealed trait Error extends ScaledaError
  object Error {

    case class IncorrectFieldCount[A : ClassTag](inputSize: Int, requiredSize: Int, fields: Seq[Field]) extends Error {
      private val ct = classTag[A]
      val msg = s"${ct.runtimeClass.getSimpleName} requires $requiredSize fields for decoding; received $inputSize fields in $fields."
    }

    case class NotParsable(value: String, reason: String) extends Error {
      val msg = s"""Input value "$value" not parsable: $reason."""
    }

    case class NotParsableAs[A : ClassTag](value: String) extends Error {
      private val ct = classTag[A]
      val msg = s"""Input value "$value" not parsable as a ${ct.runtimeClass.getSimpleName}."""
    }

  }

  implicit class EitherThrowableOps[A](either: Either[Throwable, A]) {
    def toMultiFieldParserResult(field: Field): Result[A] =
      either.leftMap(t => MultiFieldParser.Error.NotParsable(field, t.getMessage))
  }

  def apply[A](implicit dec: MultiFieldParser[A]): MultiFieldParser[A] = dec

  def instance[A](ofFields: Int)(f: Seq[Field] => Result[A]): MultiFieldParser[A] = new MultiFieldParser[A] {
    override def parse(fields: Seq[Field]): Result[A] = {
      val inputSize = fields.size
      if (inputSize == fieldCount) f(fields) else Error.IncorrectFieldCount(inputSize, fieldCount, fields).asLeft[A]
    }
    override def fieldCount: Int = ofFields
  }

  def headInstance[A](f: Field => Result[A]): MultiFieldParser[A] = instance(ofFields = 1)(fields => f(fields.head))

  implicit val hnilParser: MultiFieldParser[HNil] = instance(ofFields = 0)(_ => HNil.asRight[Error])

  implicit def hlistParser[H, T <: HList](implicit
    lazyHDec: Lazy[MultiFieldParser[H]],
    tDec: MultiFieldParser[T]
  ): MultiFieldParser[H :: T] = {
    val hDec = lazyHDec.value
    val headFieldCount = hDec.fieldCount
    instance(headFieldCount + tDec.fieldCount) { fields =>
      val (hFields, tFields) = fields.splitAt(headFieldCount)
      for {
        head <- hDec.parse(hFields)
        tail <- tDec.parse(tFields)
      } yield head :: tail
    }
  }

}
