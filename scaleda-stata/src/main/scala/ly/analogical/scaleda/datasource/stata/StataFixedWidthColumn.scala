package ly.analogical.scaleda
package datasource
package stata

import model.FixedWidthColumn

case class StataFixedWidthColumn[D](override val name: String,
                                    startCharIndex: Int,
                                    width: Int,
                                    colIndex: Int,
                                    dataType: String) extends FixedWidthColumn[D] {
  override def endCharIndex: Int = startCharIndex + width - 1
}
