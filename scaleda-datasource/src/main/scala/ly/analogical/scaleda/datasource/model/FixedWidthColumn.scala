package ly.analogical.scaleda
package datasource
package model

trait FixedWidthColumn[D] extends Column[D] {
  def startCharIndex: Int
  def endCharIndex: Int
}

trait RawFixedWidthColumn extends FixedWidthColumn[String]
