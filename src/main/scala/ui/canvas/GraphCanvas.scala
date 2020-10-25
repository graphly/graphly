package ui.canvas

import model.sim
import scalafx.beans.property.ObjectProperty
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import ui.{Controlled, Controller}
import util.Number.Implicit.DoubleExtensions

class GraphCanvas(override val controller: Controller[Iterable[GraphCanvas.DrawAction] => Unit])
  extends Canvas
    with Controlled[Iterable[GraphCanvas.DrawAction] => Unit] {
  onMousePressed = _ => requestFocus()

  val fill: ObjectProperty[Color] = ObjectProperty(Color.White)

  def redraw(shapes: Iterable[GraphCanvas.DrawAction]): Unit = {
    graphicsContext2D.fill = fill()
    graphicsContext2D.fillRect(0, 0, width.value, height.value)

    shapes.foreach(_(graphicsContext2D))
  }

  override val state: Iterable[GraphCanvas.DrawAction] => Unit = redraw
}

object GraphCanvas {
  type DrawAction = GraphicsContext => ()

  implicit object DrawActionDraw extends Draw[DrawAction] {
    override def selectionBox(start: sim.Position, end: sim.Position): DrawAction = { context =>
      val topLeft = start min end
      val delta = (start max end) - topLeft
      context.stroke = Color.Goldenrod
      context.lineWidth = 2
      context.strokeRect(topLeft.x, topLeft.y, delta.x, delta.y)
    }

    private def drawCircle(position: sim.Position, radius: Double, color: Color, gc: GraphicsContext): Unit = {
      gc.fill = color
      gc.fillArc(position.x - radius, position.y - radius, 2 * radius, 2 * radius, 0, 360, ArcType.Chord)
    }

    override val node: Draw.Shape[sim.Node, DrawAction] = new Draw.Shape[sim.Node, DrawAction] {
      private val radius = 20
      private val highlighting = Color.GreenYellow
      private val fill = Color.CornflowerBlue

      override def apply(node: sim.Node, highlight: Boolean): DrawAction = { context =>
        if (highlight) drawCircle(node.position, radius * 1.3, highlighting, context)
        drawCircle(node.position, radius, fill, context)
      }

      override def hits(node: sim.Node, hit: sim.Position): Boolean = {
        val dist = (node.x - hit.x) ** 2 + (node.y - hit.y) ** 2
        dist < radius ** 2
      }
    }

    override val connection: Draw.Shape[sim.Connection, DrawAction] = new Draw.Shape[sim.Connection, DrawAction] {
      private val width = 5
      private val highlighting = Color.GreenYellow
      private val fill = Color.Black

      override def apply(connection: sim.Connection, highlight: Boolean): DrawAction = { context =>
        if (highlight) {
          context.stroke = highlighting
          context.lineWidth = width * 2
          context.strokeLine(connection.source.x, connection.source.y, connection.target.x, connection.target.y)
        }

        context.stroke = fill
        context.lineWidth = width
        context.strokeLine(connection.source.x, connection.source.y, connection.target.x, connection.target.y)
      }

      override def hits(connection: sim.Connection, hit: sim.Position): Boolean = {
        if (!hit.inRectangle(connection.source.position, connection.target.position)) return false
        val fromStart = hit - connection.source.position
        val edgeDisplacement = connection.target.position - connection.source.position
        val normal = fromStart - edgeDisplacement * ((fromStart `.` edgeDisplacement) / (edgeDisplacement.magnitude ** 2))
        normal.magnitude < width
      }
    }
  }
}