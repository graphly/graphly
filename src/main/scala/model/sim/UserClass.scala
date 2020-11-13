package model.sim

case class UserClass(
    name: String,
    priority: Int,
    referenceSource: Node,
    `type`: UserClass.Type,
    population: Int,
    distribution: Any
//    distribution: Distribution
)

object UserClass {
  sealed trait Type
  case object Open   extends Type
  case object Closed extends Type
}
