package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import ui.Controlled
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

  override val state: Redraw[DrawAction] = redraw
}

object GraphingCanvas                   {
  type DrawAction  = GraphicsContext => Unit
  type DrawActions = Iterable[DrawAction]
}
