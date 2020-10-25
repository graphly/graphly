package model.sim

case class UserClass(name: String, priority: Int, referenceSource: Node, `type`: UserClass.Type)

object UserClass {
  sealed trait Type
  case object Open extends Type
  case object Closed extends Type
}