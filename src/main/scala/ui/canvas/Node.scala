package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType

class Node(val X: Double, val Y: Double) extends Shape {
  private val nodeRadius = 50
  private val selectColor = Color.GreenYellow
  private val fillColor = Color.CornflowerBlue

  var selected: Boolean = false

  def drawCircle(x: Double, y: Double, r: Double, c: Color, gc: GraphicsContext) = {
    gc.fill = c
    gc.fillArc(X - nodeRadius / 2,
      Y - nodeRadius / 2,
      nodeRadius, nodeRadius, 0, 360, ArcType.Chord)
  }

  override def Draw(gc: GraphicsContext) = {
    if (selected) drawCircle(X, Y, nodeRadius * 1.1, selectColor, gc)
    drawCircle(X, Y, nodeRadius, selectColor, gc)
  }

  def HitTest(x: Double, y: Double): Boolean = {
    val dist = (X - x) * (X - x) + (Y - y) * (Y - y)
    return (dist < nodeRadius * nodeRadius)
  }
}
