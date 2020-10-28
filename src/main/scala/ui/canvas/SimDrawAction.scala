package ui.canvas

import model.{Position, sim}
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import ui.canvas.GraphCanvas.DrawAction
import util.Number.Implicit.DoubleExtensions

object SimDrawAction {

  implicit object SimDrawActionDraw extends Draw[DrawAction, sim.Shape] {
    override def selectionBox(start: Position, end: Position): DrawAction = {
      context =>
        val topLeft = start min end
        val delta   = (start max end) - topLeft
        context.stroke = Color.Goldenrod
        context.lineWidth = 2
        context.strokeRect(topLeft.x, topLeft.y, delta.x, delta.y)
    }

    override val shape: this.Shape[sim.Shape]                             = new this.Shape[sim.Shape] {
      override def apply(shape: sim.Shape, highlight: Boolean): DrawAction =
        shape match {
          case node: sim.Node => Node(node, highlight)
          case connection: sim.Connection => Connection(connection, highlight)
        }

      override def hits(shape: sim.Shape, hit: Position): Boolean =
        shape match {
          case node: sim.Node => Node.hits(node, hit)
          case connection: sim.Connection => Connection.hits(connection, hit)
        }
    }

    implicit object Node extends this.Shape[sim.Node] {
      private val radius       = 20
      private val highlighting = Color.GreenYellow
      private val fill         = Color.CornflowerBlue

      private def drawCircle(
          position: Position,
          radius: Double,
          color: Color,
          gc: GraphicsContext
      ): Unit = {
        gc.fill = color
        gc.fillArc(
          x = position.x - radius,
          y = position.y - radius,
          w = 2 * radius,
          h = 2 * radius,
          startAngle = 0,
          arcExtent = 360,
          closure = ArcType.Chord
        )
      }

      def classColor(instance: Any): Color                               = {
        val conversion    = 0.5 / 26
        val name          = instance.getClass.getSimpleName
        def index(n: Int) = 0.5 + (name(n).toUpper.toInt % 26) * conversion
        Color.color(index(0), index(1), index(2))
      }

      override def apply(node: sim.Node, highlight: Boolean): DrawAction = {
        context =>
          if (highlight)
            drawCircle(node.position, radius * 1.3, highlighting, context)
          drawCircle(node.position, radius, classColor(node), context)
          context.fill = Color.Black
          val textSizeOffset = 5
          context.fillText(
            node.toString.substring(0, 2),
            node.x - textSizeOffset * 1.5,
            node.y + textSizeOffset
          )
      }

      override def hits(node: sim.Node, hit: Position): Boolean          = {
        val dist = (node.x - hit.x) ** 2 + (node.y - hit.y) ** 2
        dist < radius ** 2
      }
    }

    implicit object Connection extends this.Shape[sim.Connection] {
      private val width        = 5
      private val highlighting = Color.GreenYellow
      private val fill         = Color.Black

      override def apply(
          connection: sim.Connection,
          highlight: Boolean
      ): DrawAction = { context =>
        if (highlight) {
          context.stroke = highlighting
          context.lineWidth = width * 2
          context.strokeLine(
            connection.source.x,
            connection.source.y,
            connection.target.x,
            connection.target.y
          )
        }

        context.stroke = fill
        context.lineWidth = width
        context.strokeLine(
          connection.source.x,
          connection.source.y,
          connection.target.x,
          connection.target.y
        )
      }

      override def hits(connection: sim.Connection, hit: Position): Boolean = {
        if (
          !hit
            .inRectangle(connection.source.position, connection.target.position)
        ) return false
        val fromStart        = hit - connection.source.position
        val edgeDisplacement = connection.target.position -
          connection.source.position
        val normal           = fromStart -
          edgeDisplacement *
          ((fromStart `.` edgeDisplacement) / (edgeDisplacement.magnitude ** 2))
        normal.magnitude < width
      }

    }
  }
}
