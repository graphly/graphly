package ui.canvas

import model.{LinearTransform, Position, sim}
import scalafx.Includes._
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import scalafx.scene.paint.Color
import scalafx.scene.shape.{ArcType, SVGPath}
import scalafx.scene.text.{Text, TextAlignment}
import ui.canvas.GraphingCanvas.DrawAction
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
          case trace: sim.Trace => Trace(trace, highlight)
        }

      override def hits(shape: sim.Shape, hit: Position): Boolean =
        shape match {
          case node: sim.Node => Node.hits(node, hit)
          case connection: sim.Connection => Connection.hits(connection, hit)
          case trace: sim.Trace => Trace.hits(trace, hit)
        }
    }

    implicit object Node extends this.Shape[sim.Node] {
      val radius: Int                                                  = 20
      private val highlighting                                         = Color.GreenYellow
      private val icons: Map[Class[_ <: sim.Node], String] = Map(
        classOf[sim.Source] -> "/assets/icons/source.svg",
        classOf[sim.Join]   -> "/assets/icons/join.svg",
        classOf[sim.Queue]  -> "/assets/icons/queue.svg",
        classOf[sim.Fork]   -> "/assets/icons/fork.svg",
        classOf[sim.Sink]   -> "/assets/icons/sink.svg",
      )

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

      def classColor(instance: Any): Color = {
        val conversion    = 0.5 / 26
        val name          = instance.getClass.getSimpleName
        def index(n: Int) = 0.5 + (name(n).toUpper.toInt % 26) * conversion
        Color.color(index(0), index(1), index(2))
      }

      final private def drawName(context: GraphicsContext, node: sim.Node): Unit = {
        val textObj = new Text(node.name)
        textObj.font = context.font

        val (x, y) = node.position.coords
        val textY = y + 2 * radius
        val (tw, th) = (textObj.layoutBounds().getWidth, textObj.layoutBounds().getHeight)
        context.fill = Color.Black
        context.clearRect(x - tw / 2 - 5, textY - th / 2 - 5, tw + 10, th + 5)

        context.textAlign = TextAlignment.Center
        context.fillText(node.name, x, textY)
      }

      override def apply(node: sim.Node, highlight: Boolean): DrawAction = {
        context =>
          if (highlight)
            drawCircle(node.position, radius * 1.3, highlighting, context)
          drawCircle(node.position, radius, classColor(node), context)

          val resourceURI = icons(node.getClass)
          val img = new Image(resourceURI, 2 * radius, 2 * radius, false, false)
          context.drawImage(img, node.x - radius, node.y - radius)

          drawName(context, node)
      }

      override def hits(node: sim.Node, hit: Position): Boolean = {
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

        val delta = (connection.target.position - connection.source.position)
          .unit * Node.radius
        val head  = connection.target.position - delta
        val angle = math.Pi / 6
        val end   = head - delta * math.cos(angle)

        context.stroke = fill
        context.lineWidth = width
        context
          .strokeLine(connection.source.x, connection.source.y, end.x, end.y)

        context.fill = fill
        context.fillPolygon(Seq(
          head.coords,
          (head - LinearTransform.radians(angle) * delta).coords,
          (head - LinearTransform.radians(-angle) * delta).coords
        ))
      }

      override def hits(connection: sim.Connection, hit: Position): Boolean = {
        if (
          !hit.inRectangle(
            connection.source.position,
            connection.target.position,
            fuzzy = width
          )
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

    implicit object Trace extends this.Shape[sim.Trace] {
      private val highlighting       = Color.GreenYellow
      private val highlightingBorder = 3
      private val resizeToggleSize   = 10

      override def hits(trace: sim.Trace, hit: Position): Boolean =
        hit.inRectangle(trace.position, trace.end)

      override def apply(trace: sim.Trace, highlight: Boolean): DrawAction = {
        context =>
          if (highlight) {
            context.fill = highlighting
            context.fillRect(
              trace.x - highlightingBorder,
              trace.y - highlightingBorder,
              trace.width + 2 * highlightingBorder,
              trace.height + 2 * highlightingBorder
            )
          }
          val image = new Image(
            trace.image.stream,
            requestedWidth = trace.width,
            requestedHeight = trace.height,
            preserveRatio = false,
            smooth = true
          )
          context.drawImage(image, trace.x, trace.y)
          if (highlight) {
            context.fill = highlighting
            var Position(x, y) = trace.`end`
            x -= resizeToggleSize
            y -= resizeToggleSize
            context.fillRect(x, y, resizeToggleSize, resizeToggleSize)
          }
      }
    }
  }
}
