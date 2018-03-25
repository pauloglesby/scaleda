package ly.analogical.scaleda

trait ScaledaError extends Product with Serializable {
  def msg: String
}
