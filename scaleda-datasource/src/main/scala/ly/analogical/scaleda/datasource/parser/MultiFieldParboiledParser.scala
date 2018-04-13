package ly.analogical.scaleda
package datasource
package parser

import org.parboiled2.{CharPredicate, Parser, ParserInput, Rule}
import shapeless.HNil

trait MultiFieldParboiledParser extends Parser {
  def digits: Rule[HNil, HNil] = rule(oneOrMore(CharPredicate.Digit))
}

object MultiFieldParboiledParser {

  case class EmbeddedIntParser[A](prefix: String, input: ParserInput, f: Int => A) extends MultiFieldParboiledParser {
    def extract: Rule[HNil, shapeless.::[A, HNil]] = rule(prefix ~ capture(digits) ~> (_.toInt) ~> f)
  }

}
