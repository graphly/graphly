package ui.canvas
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

class Edge(val start: Node, val end: Node) extends Shape {
  private val strokeColor = Color.Black

  override def Draw(gc: GraphicsContext): Unit = {
    gc.stroke = strokeColor

    gc.lineWidth = 5
    gc.strokeLine(start.X, start.Y, end.X, end.Y)
  }
}
