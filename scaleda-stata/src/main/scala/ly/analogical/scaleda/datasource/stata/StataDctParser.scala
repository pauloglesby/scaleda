package ly.analogical.scaleda
package datasource
package stata

import parser.MultiFieldParser
import parser.MultiFieldParboiledParser

import cats.syntax.either._

import scala.util.Try

object StataDctParser {

  import MultiFieldParser._
  import MultiFieldParboiledParser._

  implicit val nameParser: MultiFieldParser[StataDctField.Name] =
    headInstance(StataDctField.Name(_).asRight[MultiFieldParser.Error])

  implicit val startsAtParser: MultiFieldParser[StataDctField.StartsAt] =
    headInstance { field =>
      val maybeStartsAt: Try[StataDctField.StartsAt] = EmbeddedIntParser("_column", field, StataDctField.StartsAt).extract.run()
      Either.fromTry(maybeStartsAt).toMultiFieldParserResult(field)
    }

  implicit val dataTypeParser: MultiFieldParser[StataDctField.DataType] =
    headInstance(StataDctField.DataType(_).asRight[MultiFieldParser.Error])

  implicit val widthParser: MultiFieldParser[StataDctField.Width] =
    headInstance { field =>
      val maybeWidth: Try[StataDctField.Width] = EmbeddedIntParser("%", field, StataDctField.Width).extract.run()
      Either.fromTry(maybeWidth).toMultiFieldParserResult(field)
    }
}
