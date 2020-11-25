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
      private val icons: Map[Class[_ <: sim.Node], (String, Position)] = Map(
        classOf[sim.Source] ->
          (
            "M 15.430082,3.8560432 H 22.11222 V 15.821065 h 11.614109 v 6.063249 H 22.11222 V 33.769486 H 15.430082 V 21.884314 H 3.8938378 V 15.821065 H 15.430082 Z",
            Position(1, 1)
          ),
        classOf[sim.Join] ->
          (
            "M -8.4730901,-10.351542 5.3283416,1.8108128 H 22.507498 V 7.7200146 H 5.4070685 L -7.2959487,20.051525 -11.391579,15.803817 -0.12812598,4.8706667 -12.362534,-5.9121233 Z",
            Position(18, 17)
          ),
        classOf[sim.Queue] ->
          (
            "M 9.3069618,14.220668 5.5226877,24.614841 H 2.4996184 v 2.938634 h 1.9539715 l -1.8486396,5.078961 5.8440041,2.128387 2.6234896,-7.207348 h 1.68062 l -2.271246,6.237613 5.841872,2.126255 3.043965,-8.363868 h 1.30748 l -2.621358,7.200953 5.844003,2.128386 3.396209,-9.329339 h 2.29001 v 2.145019 l 6.258082,-3.618387 C 33.751586,24.884504 31.67116,23.671662 29.583999,22.470248 v 2.144593 h -1.220484 l 2.23585,-6.144221 -5.844002,-2.127961 -3.010704,8.272182 h -1.30748 l 2.588095,-7.109692 -5.841871,-2.126256 -3.361241,9.235948 h -1.68062 l 3.008997,-8.266212 z",
            Position(2, -5)
          ),
        classOf[sim.Fork] ->
          (
            "m 27.054108,7.3018813 2.990784,3.4136867 -7.48777,6.600469 11.266966,-0.07226 0.02162,3.637702 -11.20787,0.06648 6.660438,6.466061 -3.149332,3.266262 -9.767968,-9.482302 H 3.2316169 V 16.654111 H 16.44151 Z",
            Position(2, 2)
          ),
        classOf[sim.Sink] ->
          (
            "m 15.711818,6.929779 c 2.169281,0.090609 4.408082,0.8581807 6.305524,2.7161719 1.780806,1.7437801 3.239623,4.3779041 4.278971,8.1516381 1.552099,0.159395 2.982398,0.439788 4.197971,0.862177 0.955627,0.332063 1.790458,0.734683 2.51411,1.32919 0.723652,0.594508 1.425285,1.531664 1.425285,2.727106 0,1.195441 -0.701633,2.138844 -1.425285,2.733352 -0.723652,0.594508 -1.558483,0.989318 -2.51411,1.32138 -1.911257,0.664125 -4.315269,1.021493 -6.970657,1.021493 -2.655389,0 -5.065631,-0.357368 -6.976887,-1.021493 -0.955629,-0.332062 -1.788901,-0.726872 -2.512554,-1.32138 -0.723651,-0.594508 -1.425285,-1.537911 -1.425285,-2.733352 0,-1.195442 0.701634,-2.132598 1.425285,-2.727106 0.723653,-0.594507 1.556925,-0.997127 2.512554,-1.32919 1.911256,-0.664125 4.321498,-1.015244 6.976887,-1.015244 0.06872,0 0.13256,0.0058 0.200943,0.0063 -0.933216,-3.014773 -2.130887,-5.02338 -3.403551,-6.269583 C 18.396285,9.4965231 16.282826,9.1321769 14.074688,9.4335305 9.6584078,10.036238 5.301784,13.706932 5.301784,13.706932 L 3.7129414,11.871681 c 0,0 4.5814723,-4.101985 10.0377466,-4.8466251 0.341016,-0.04654 0.687834,-0.078315 1.037421,-0.093713 0.305887,-0.013465 0.613812,-0.014494 0.923709,-0.00157 z m 7.811809,14.639837 c -2.288443,0 -4.364768,0.341166 -5.693353,0.802824 -0.350561,0.121814 -0.465684,0.22771 -0.694729,0.348305 0.228731,0.120276 0.345348,0.222222 0.694729,0.343619 1.328585,0.461659 3.404911,0.802827 5.693353,0.802827 2.288443,-2e-6 4.358539,-0.341166 5.687122,-0.802827 0.349382,-0.121401 0.466,-0.223348 0.694729,-0.343619 -0.229043,-0.120595 -0.344168,-0.226491 -0.694729,-0.348305 -0.555858,-0.193149 -1.263424,-0.35563 -2.040572,-0.490442 0.04877,0.297183 0.106299,0.573021 0.151092,0.880921 l -2.395722,0.348306 c -0.07674,-0.527474 -0.168576,-1.009483 -0.257019,-1.499439 -0.377468,-0.01901 -0.751905,-0.04217 -1.144901,-0.04217 z",
            Position(1, 1)
          )
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

      final private def drawSVG(
          context: GraphicsContext,
          svgPath: SVGPath
      ): Unit                              = {
        context.beginPath()
        context.translate(svgPath.translateX(), svgPath.translateY())
        context.fill = svgPath.fill()
        context.stroke = svgPath.stroke()
        context.appendSVGPath(svgPath.content())
        context.fillPath()
        context.translate(-svgPath.translateX(), -svgPath.translateY())
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

          val icon              = new SVGPath()
          val (content, offset) = icons(node.getClass)
          icon.content = content
          icon.translateX = node.x - radius + offset.x
          icon.translateY = node.y - radius + offset.y
          icon.resize(100, 100)
          icon.fill = Color.Black

          drawName(context, node)
          drawSVG(context, icon)
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
