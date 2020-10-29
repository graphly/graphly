package ui.canvas

import scalafx.scene.canvas.{Canvas, GraphicsContext}
import ui.canvas.GraphCanvas.DrawAction
import ui.{Controlled, Controller}

class GraphCanvas(override val controller: Controller[Unit]) extends Canvas with Controlled[Unit] {
  onMousePressed = _ => requestFocus()

  override val state: () = ()

  def redraw(shapes: Iterable[DrawAction]): Unit = {
    graphicsContext2D.clearRect(0, 0, width(), height())

    shapes.foreach(_(graphicsContext2D))
  }
}

object GraphCanvas {
  type DrawAction = GraphicsContext => ()
}
