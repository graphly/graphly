package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType

class Node(val X: Double, val Y: Double) extends Shape {
  private val nodeRadius = 50
  private val fillColor = Color.CornflowerBlue

  override def Draw(gc: GraphicsContext) = {
    gc.fill = fillColor
    gc.fillArc(X - nodeRadius / 2,
               Y - nodeRadius / 2,
               nodeRadius, nodeRadius, 0, 360, ArcType.Chord)
  }
}
