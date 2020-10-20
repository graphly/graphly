package model.sim

import scala.collection.mutable

// Assume all parameters are the default generated by JSimGraph, we'll add parameters next week
sealed trait Node {
  var position: Position
}
case class Source(var position: Position) extends Node
case class Queue(var position: Position) extends Node
case class Sink(var position: Position) extends Node
case class Fork(var position: Position) extends Node
case class Join(var position: Position) extends Node

case class Position(x: Double, y: Double)

object Position {
  object Implicits {
    implicit def pairToPosition(pair: (Double, Double)): Position = Position(pair._1, pair._2)
  }
}

case class Connection(source: Node, target: Node)

case class UserClass(name: String, priority: Int, referenceSource: Node, `type`: UserClass.Type)

object UserClass {
  sealed trait Type
  case object Open extends Type
  case object Closed extends Type
}

case class Measure(alpha: Float, referenceNode: Node, referenceClass: UserClass, `type`: String, verbose: Boolean)

case class Sim(nodes: mutable.Set[Node],
               connections: mutable.Set[Connection],
               classes: mutable.Set[UserClass],
               measures: mutable.Set[Measure],
               configuration: Sim.Configuration = mutable.HashMap.empty)

object Sim {
  type Configuration = mutable.HashMap[String, Any]

  def empty: Sim = {
    Sim(
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashMap.empty
    )
  }
}