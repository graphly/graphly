package ui.canvas

import model.Position
import scalafx.scene.input.{InputEvent, MouseEvent, ScrollEvent}
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import ui.{Controlled, LogicalEvent}
import ui.WithPosition.Implicit._
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

  children = List(traceCanvas, canvas)

  def redraw(
      shapes: Option[DrawActions],
      background: Option[DrawActions]
  ): Unit = {
    shapes.foreach(canvas.redraw)
    background.foreach(traceCanvas.redraw)
  }

  // Add transformation to canvases
  def transformCanvas(
      delta: Option[Position],
      zoom: Option[(Position, Double)]
  ): Unit = {
    delta foreach { delta =>
      canvas.translate(delta)
      traceCanvas.translate(delta)
    }

    zoom foreach {
      case (center, amount) =>
        canvas.zoom(center, amount)
        traceCanvas.zoom(center, amount)
    }
  }

  override val state: Redraw[DrawAction] = redraw

  override def transform(e: ui.Position): model.Position = {
    val affine = canvas.graphicsContext2D.getTransform
    affine.inverseTransform(e.x, e.y).position.model
  }
}

object GraphingCanvas {
  type DrawAction  = GraphicsContext => Unit
  type DrawActions = Iterable[DrawAction]
}
