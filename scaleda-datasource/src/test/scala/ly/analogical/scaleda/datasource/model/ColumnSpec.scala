package ly.analogical.scaleda
package datasource
package model

import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class ColumnSpec extends BaseSpec with GeneratorDrivenPropertyChecks {

  "Column" should {

    "default to a name equal to the provided integer index" in {
      forAll(Gen.choose(0, 100)) { index =>
        val column = new Column[String] {
          override def colIndex: Int = index
        }
        column.name should equal(index.toString)
      }
    }

  }

}
