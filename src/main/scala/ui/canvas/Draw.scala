package ui.canvas

import model.sim.{Connection, Node, Position}

trait Draw[D] {
  val node: Draw.Shape[Node, D]
  val connection: Draw.Shape[Connection, D]

  def selectionBox(start: Position, end: Position): D
}

object Draw {

  trait Shape[S, D] {
    def apply(shape: S, highlight: Boolean): D

    def hits(shape: S, position: Position): Boolean
  }

  object Implicit {
    implicit class ShapeNode[D](node: Node)(implicit draw: Draw[D]) {
      def hits(position: Position): Boolean = draw.node.hits(node, position)
    }

    implicit class ShapeConnection[D](connection: Connection)(implicit
        draw: Draw[D]
    )                                                               {
      def hits(position: Position): Boolean =
        draw.connection.hits(connection, position)
    }
  }
}
