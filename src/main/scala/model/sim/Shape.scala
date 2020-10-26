package model.sim

sealed trait Shape

// Assume all parameters are the default generated by JSimGraph, we'll add parameters next week
sealed trait Node                         extends Shape with Positioned {
  var position: Position
  var name: String
}
case class Source(var position: Position, var name: String) extends Node
case class Queue(var position: Position, var name: String)  extends Node
case class Sink(var position: Position, var name: String)   extends Node
case class Fork(var position: Position, var name: String)   extends Node
case class Join(var position: Position, var name: String)   extends Node

case class Connection(source: Node, target: Node) extends Shape
