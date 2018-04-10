package ly.analogical.scaleda
package datasource
package stata

import parser.MultiFieldParser

import cats.syntax.either._
import org.parboiled2._
import shapeless.HNil

import scala.util.Try

object StataDctParser {

  import MultiFieldParser._

  // TODO move this to datasource module
  case class EmbeddedIntParser[A](prefix: String, input: ParserInput, f: Int => A) extends StataDctParboiledParser {
    def extract: Rule[HNil, shapeless.::[A, HNil]] = rule(prefix ~ capture(digits) ~> (_.toInt) ~> f)
  }

  implicit val nameParser: MultiFieldParser[StataDctField.Name] =
    headInstance(StataDctField.Name(_).asRight[MultiFieldParser.Error])

  implicit val startsAtParser: MultiFieldParser[StataDctField.StartsAt] =
    headInstance { field =>
      val maybeStartsAt: Try[StataDctField.StartsAt] = EmbeddedIntParser("_column", field, StataDctField.StartsAt).extract.run()
      Either.fromTry(maybeStartsAt).leftMap(t => MultiFieldParser.Error.NotParsable(field, t.getMessage))
    }

  implicit val dataTypeParser: MultiFieldParser[StataDctField.DataType] =
    headInstance(StataDctField.DataType(_).asRight[MultiFieldParser.Error])

  implicit val widthParser: MultiFieldParser[StataDctField.Width] =
    headInstance { field =>
      val maybeWidth: Try[StataDctField.Width] = EmbeddedIntParser("%", field, StataDctField.Width).extract.run()
      // TODO DRY this up
      Either.fromTry(maybeWidth).leftMap(t => MultiFieldParser.Error.NotParsable(field, t.getMessage))
    }
}

// TODO move this to MultiFieldParboiledParser
trait StataDctParboiledParser extends Parser {
  def digits: Rule[HNil, HNil] = rule(oneOrMore(CharPredicate.Digit))
}
