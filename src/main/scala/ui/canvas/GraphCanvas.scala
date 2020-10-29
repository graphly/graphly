package ui.canvas

import scalafx.beans.property.ObjectProperty
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.paint.Color
import ui.canvas.GraphCanvas.DrawAction
import ui.{Controlled, Controller}

class GraphCanvas(override val controller: Controller[Iterable[DrawAction] => Unit])(
    implicit draw: Draw[DrawAction, _]
) extends Canvas
    with Controlled[Iterable[DrawAction] => Unit] {
  onMousePressed = _ => requestFocus()

  override val state: Iterable[DrawAction] => Unit = redraw
  val fill: ObjectProperty[Color]         = ObjectProperty(Color.White)

  def redraw(shapes: Iterable[DrawAction]): Unit = {
    graphicsContext2D.fill = fill()
    graphicsContext2D.fillRect(0, 0, width.value, height.value)

    shapes.foreach(_(graphicsContext2D))
  }
}

object GraphCanvas {
  type DrawAction = GraphicsContext => ()
}
