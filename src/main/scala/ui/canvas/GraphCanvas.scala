package ui.canvas

import javafx.scene.layout.Background
import scalafx.beans.binding.{Bindings, ObjectBinding}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import ui.util.Background.Implicit._
import ui.{Controlled, Controller}

class GraphCanvas(override val controller: Controller[Iterable[Shape] => Unit])
  extends Canvas
    with Controlled[Iterable[Shape] => Unit] {
  onMousePressed = _ => requestFocus()

  val fill: ObjectProperty[Color] = ObjectProperty(Color.White)
  val background: ObjectBinding[Background] = Bindings.createObjectBinding(() => backgroundFromColor(fill()), fill)

  def redraw(shapes: Iterable[Shape]): Unit = {
    graphicsContext2D.fill = fill()
    graphicsContext2D.fillRect(0, 0, width.value, height.value)

    shapes.foreach(_.draw(graphicsContext2D))
  }

  override val state: Iterable[Shape] => Unit = redraw
}
