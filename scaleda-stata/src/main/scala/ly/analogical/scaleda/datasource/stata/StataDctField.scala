package ly.analogical.scaleda
package datasource
package stata

sealed trait StataDctField extends Product with Serializable
object StataDctField {
  final case class StartsAt(value: Int) extends StataDctField
  final case class Name(value: String) extends StataDctField
  final case class Width(value: Int) extends StataDctField
  // TODO make this a separate ADT and use Coproduct
  final case class DataType(value: String) extends StataDctField
}

/*

  3. permute output to get Repr of FixedWidthColumn
  4. tests! coverage!

  1. Create fixed width file parser...
  2. Use Coproduct for DataType (i.e. make `DataType` non-stringly-typed

 */
