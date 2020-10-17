package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import ui.Position
import util.Number.DoubleExtensions

case class Edge(start: Node, end: Node) extends Shape {
  private val strokeColor = Color.Black
  private val highlightColor = Color.GreenYellow
  override var highlight: Boolean = false
  private val width: Double = 5

  override def draw(context: GraphicsContext): Unit = {
    if (highlight) {
      context.stroke = highlightColor
      context.lineWidth = width * 2
      context.strokeLine(start.x, start.y, end.x, end.y)
    }

    context.stroke = strokeColor
    context.lineWidth = width
    context.strokeLine(start.x, start.y, end.x, end.y)
  }

  override def hitBy(hit: Position): Boolean = {
    if (!hit.inRectangle(start.position, end.position)) return false
    val fromStart = start.position - hit
    val edgeDisplacement = end.position - start.position
    val normal = fromStart - edgeDisplacement * ((fromStart `.` edgeDisplacement) / (edgeDisplacement.magnitude ** 2))
    normal.magnitude < width
  }
}
