package ui.canvas
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

class Edge(val start: Node, val end: Node) extends Shape {
  private val strokeColor = Color.Black;

  override def Draw(gc: GraphicsContext): Unit = {
    gc.stroke = strokeColor

    gc.moveTo(start.X, start.Y)
    gc.lineTo(end.X, end.Y)
  }
}
