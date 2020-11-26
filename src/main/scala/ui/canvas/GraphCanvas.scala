package ui.canvas

import scalafx.scene.canvas.{Canvas, GraphicsContext}
import ui.canvas.GraphCanvas.DrawAction

class GraphCanvas extends Canvas {
  onMousePressed = _ => requestFocus()

  def redraw(shapes: Iterable[DrawAction]): Unit = {
    graphicsContext2D.clearRect(0, 0, width(), height())
    shapes.foreach(_(graphicsContext2D))
  }
}

object GraphCanvas {
  type DrawAction = GraphicsContext => ()
}
