package ui.canvas

import model.Position
import scalafx.scene.input.ScrollEvent
import scalafx.scene.input.MouseEvent
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import ui.{Controlled, GPosEvent}
import ui.Position.Implicit.{MouseEventPosition, Point2DPosition, ScrollEventPosition}
import ui.canvas.GraphCanvasController.Redraw
import ui.canvas.GraphingCanvas.{DrawAction, DrawActions}
import ui.util.Background

class GraphingCanvas(
    override val controller: GraphCanvasController[DrawAction]
)(implicit draw: Draw[DrawAction, _])
    extends Pane
    with Controlled[Redraw[DrawAction]] {

  val canvas      = new GraphCanvas
  val traceCanvas = new GraphCanvas
  canvas.width <== this.width
  canvas.height <== this.height
  traceCanvas.width <== this.width
  traceCanvas.height <== this.height

  background = Background(Color.White)

  children =
    List(traceCanvas, canvas, FloatingPropertiesPanel.Element(controller))

  def redraw(
      shapes: Option[DrawActions],
      background: Option[DrawActions]
  ): Unit = {
    shapes.foreach(canvas.redraw)
    background.foreach(traceCanvas.redraw)
  }

  // Add transformation to canvases
  def transformCanvas(
      oDelta: Option[Position],
      oZoom: Option[(Position, Double)]
  ): Unit = {
    oDelta match {
      case Some(delta) => {
        canvas.translate(delta)
        traceCanvas.translate(delta)
      }

      case None =>
    }

    oZoom match {
      case Some((center, amount)) => {
        canvas.zoom(center, amount)
        traceCanvas.zoom(center, amount)
      }

      case None =>
        if (oDelta.isEmpty) {
          canvas.resetTransform()
          traceCanvas.resetTransform()
        }
    }
  }

  override val state: Redraw[DrawAction] = redraw

  override def augmentMouseEvent(e: MouseEvent): GPosEvent[MouseEvent] = {
    val xform   = canvas.graphicsContext2D.getTransform
    val xCoords = xform.inverseTransform(e.x, e.y)

    new GPosEvent[MouseEvent](xCoords.position.model, e.position.model, e)
  }

  override def augmentScrollEvent(e: ScrollEvent): GPosEvent[ScrollEvent] = {
    val xform = canvas.graphicsContext2D.getTransform
    val xCoords = xform.inverseTransform(e.x, e.y)

    new GPosEvent[ScrollEvent](xCoords.position.model, e.position.model, e)
  }
}

object GraphingCanvas {
  type DrawAction  = GraphicsContext => Unit
  type DrawActions = Iterable[DrawAction]
}
