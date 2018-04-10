package ly.analogical.scaleda
package datasource
package model

trait Column[D] {
  type Data = D
  def colIndex: Int
  def name: String = colIndex.toString
}

trait RawColumn extends Column[String]
