package model.sim

case class Measure(
    alpha: Float,
    name: String,
    nodeType: String,
    referenceNode: Option[Node],
    referenceClass: Option[UserClass],
    `type`: String,
    precision: Float,
    verbose: Boolean
) {
  def referenceNodeName: String =
    if (referenceNode.isDefined) { referenceNode.get.name }
    else ""

  def referenceClassName: String =
    if (referenceClass.isDefined) { referenceClass.get.name }
    else ""
}
