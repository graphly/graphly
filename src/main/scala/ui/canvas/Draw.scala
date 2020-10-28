package ui.canvas

import model.Position

trait Draw[+D, A] {
  trait Shape[S <: A] {
    def apply(shape: S, highlight: Boolean): D
    def hits(shape: S, hit: Position): Boolean
  }

  val shape: Shape[A]

  def selectionBox(start: Position, end: Position): D
}

object Draw       {

  object Implicit {
    implicit class DrawShape[S](shape: S) {
      def hits[D](position: Position)(implicit draw: Draw[D, _ >: S]): Boolean =
        draw.shape.hits(shape, position)
    }
  }
}
