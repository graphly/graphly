package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType

class Node(val X: Double, val Y: Double) extends Shape {
  private val nodeRadius = 20
  private val selectColor = Color.GreenYellow
  private val fillColor = Color.CornflowerBlue

  var selected: Boolean = true

  def drawCircle(r: Double, c: Color, gc: GraphicsContext): Unit = {
    gc.fill = c
    gc.fillArc(X - r, Y - r, 2 * r, 2 * r, 0, 360, ArcType.Chord)
  }

  override def Draw(gc: GraphicsContext): Unit = {
    if (selected) drawCircle(nodeRadius * 1.3, selectColor, gc)
    drawCircle(nodeRadius, fillColor, gc)
  }

  def HitTest(x: Double, y: Double): Boolean = {
    val dist = (X - x) * (X - x) + (Y - y) * (Y - y)
    return dist < nodeRadius * nodeRadius
  }
}
