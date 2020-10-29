package ui.canvas

import scalafx.beans.property.ObjectProperty
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.layout.StackPane
import scalafx.scene.paint.Color
import ui.canvas.GraphCanvasController.Redraw
import ui.canvas.GraphingCanvas.{DrawAction, DrawActions}
import ui.util.Background
import ui.util.Property.Implicit._
import ui.{Controlled, Controller}

class GraphingCanvas(
    override val controller: GraphCanvasController[DrawAction]
)(implicit draw: Draw[DrawAction, _])
    extends StackPane
    with Controlled[Redraw[DrawAction]]
    with Controller[Unit] {
  val canvas      = new GraphCanvas(this)
  val traceCanvas = new GraphCanvas(this)
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

object GraphingCanvas     {
  type DrawAction  = GraphicsContext => Unit
  type DrawActions = Iterable[DrawAction]
}
