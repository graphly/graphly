package model.sim

case class Measure(
    alpha: Float,
    referenceNode: Node,
    referenceClass: UserClass,
    `type`: String,
    verbose: Boolean
)
