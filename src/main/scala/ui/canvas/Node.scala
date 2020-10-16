package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import util.Number._

case class Node(x: Double, y: Double, node: model.sim.Node) extends Shape {
  private val nodeRadius = 20
  private val selectColor = Color.GreenYellow
  private val fillColor = Color.CornflowerBlue

  var selected: Boolean = false

  def drawCircle(r: Double, c: Color, gc: GraphicsContext): Unit = {
    gc.fill = c
    gc.fillArc(x - r, y - r, 2 * r, 2 * r, 0, 360, ArcType.Chord)
  }

  override def draw(context: GraphicsContext): Unit = {
    if (selected) drawCircle(nodeRadius * 1.3, selectColor, context)
    drawCircle(nodeRadius, fillColor, context)
  }

  def hitTest(xHit: Double, yHit: Double): Boolean = {
    val dist = (x - xHit) ** 2 + (y - yHit) ** 2
    dist < nodeRadius * nodeRadius
  }
}
