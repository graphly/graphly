package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import ui.{Position, Positioned}
import util.Number.Implicit._

case class Node(override var position: Position, node: model.sim.Node) extends Shape with Positioned {
  private val nodeRadius = 20
  private val selectColor = Color.GreenYellow
  private val fillColor = Color.CornflowerBlue

  var highlight: Boolean = false

  def drawCircle(r: Double, c: Color, gc: GraphicsContext): Unit = {
    gc.fill = c
    gc.fillArc(position.x - r, position.y - r, 2 * r, 2 * r, 0, 360, ArcType.Chord)
  }

  override def draw(context: GraphicsContext): Unit = {
    if (highlight) drawCircle(nodeRadius * 1.3, selectColor, context)
    drawCircle(nodeRadius, fillColor, context)
  }

  override def hitBy(hit: Position): Boolean = {
    val dist = (position.x - hit.x) ** 2 + (position.y - hit.y) ** 2
    dist < nodeRadius * nodeRadius
  }
}
