package ui.canvas

import ui.Position
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

class SelectionBox(val start: Position, val end: Position) extends Shape {
  val color: Color = Color.Goldenrod
  val width: Int = 2

  override def draw(context: GraphicsContext): Unit = {
    val topLeft = start min end
    val delta = (start max end) - topLeft
    context.stroke = color
    context.lineWidth = width
    context.strokeRect(topLeft.x, topLeft.y, delta.x, delta.y)
  }

  override var highlight: Boolean = false

  override def hitBy(position: Position): Boolean = false
}
