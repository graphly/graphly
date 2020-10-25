package ui.canvas

import model.sim.{Position, Node, Connection}

trait Draw[D] {
  def selectionBox(start: Position, end: Position): D

  val node: Draw.Shape[Node, D]
  val connection: Draw.Shape[Connection, D]
}

object Draw {

  trait Shape[S, D] {
    def apply(shape: S, highlight: Boolean): D

    def hits(shape: S, position: Position): Boolean
  }

  object Implicit {
    implicit class ShapeNode[D](node: Node)(implicit draw: Draw[D]) {
//      def toDrawable[T](implicit drawable: Drawable[T]): T = drawable.node(node)

      def hits(position: Position): Boolean = draw.node.hits(node, position)
    }

    implicit class ShapeConnection[D](connection: Connection)(implicit
        draw: Draw[D]
    ) {
//      def toDrawable[T](implicit drawable: Drawable[T]): T = drawable.connection(connection)

      def hits(position: Position): Boolean =
        draw.connection.hits(connection, position)
    }
  }
}
