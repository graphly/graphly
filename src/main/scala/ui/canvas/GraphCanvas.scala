package ui.canvas

import scalafx.beans.property.ObjectProperty
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import ui.{Controlled, Controller}

class GraphCanvas(override val controller: Controller[Iterable[Shape] => Unit])
  extends Canvas
    with Controlled[Iterable[Shape] => Unit] {
  onMousePressed = _ => requestFocus()

  val fill: ObjectProperty[Color] = ObjectProperty(Color.White)

  def redraw(shapes: Iterable[Shape]): Unit = {
    graphicsContext2D.fill = fill()
    graphicsContext2D.fillRect(0, 0, width.value, height.value)

    shapes.foreach(_.draw(graphicsContext2D))
  }

  override val state: Iterable[Shape] => Unit = redraw
}
