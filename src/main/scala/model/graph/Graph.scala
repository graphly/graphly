package model.graph

// Filler, please update and replace with JMT compatible graphs, we may want to use a graph library?

sealed trait Node
case class Fork(in: Edge, left: Edge, right: Edge) extends Node
case class Join(left: Edge, right: Edge, out: Edge) extends Node
case class Sink(in: Edge) extends Node
case class Source(out: Edge) extends Node
case class Pass(in: Edge, out: Edge) extends Node

case class Edge(weight: Int, length: Int)

class Graph(val source: Source)
