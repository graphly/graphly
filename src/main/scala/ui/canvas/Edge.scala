package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

case class Edge(start: Node, end: Node) extends Shape {
  private val strokeColor = Color.Black

  override def draw(context: GraphicsContext): Unit = {
    context.stroke = strokeColor

    context.lineWidth = 5
    context.strokeLine(start.x, start.y, end.x, end.y)
  }
}
