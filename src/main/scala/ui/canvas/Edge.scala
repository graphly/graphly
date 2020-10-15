package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

case class Edge(start: Node, end: Node) extends Shape {
  private val strokeColor = Color.Black

  override def draw(gc: GraphicsContext): Unit = {
    gc.stroke = strokeColor

    gc.lineWidth = 5
    gc.strokeLine(start.x, start.y, end.x, end.y)
  }
}
