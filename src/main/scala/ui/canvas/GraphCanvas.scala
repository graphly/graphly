package ui.canvas

import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.scene.input.MouseEvent

import collection.Seq
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import ui.{Controlled, Controller}

class GraphCanvas(override val controller: Controller[Seq[Shape] => Unit])
  extends Canvas
    with Controlled[Seq[Shape] => Unit] {
  onMousePressed = _ => requestFocus()

  def redraw(shapes: Seq[Shape]): Unit = {
    graphicsContext2D.fill = Color.White
    graphicsContext2D.fillRect(0, 0, width.value, height.value)

    shapes.foreach(_.draw(graphicsContext2D))
  }

  override val state: Seq[Shape] => Unit = redraw
}
