package ui.canvas

import model.Position
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import ui.canvas.GraphCanvas.DrawAction

class GraphCanvas extends Canvas {
  onMousePressed = _ => requestFocus()

  def redraw(shapes: Iterable[DrawAction]): Unit = {
    val xform = graphicsContext2D.getTransform

    resetTransform()
    graphicsContext2D.clearRect(0, 0, width(), height())
    graphicsContext2D.setTransform(xform)

    shapes.foreach(_(graphicsContext2D))
  }

  def resetTransform(): Unit = {
    graphicsContext2D.setTransform(1, 0, 0, 1, 0, 0)
  }

  def translate(rawDelta: Position): Unit = {
    val xform = graphicsContext2D.getTransform
    val zoomLevel = xform.mxx()
    val delta = rawDelta / zoomLevel

    xform.appendTranslation(delta.x, delta.y)

    graphicsContext2D.setTransform(xform)
  }

  def zoom(center: Position, amount: Double): Unit = {
    val xform = graphicsContext2D.getTransform
    val zoomLevel = xform.mxx()
    val tgtZoomLevel = zoomLevel + amount

    if (zoomLevel < 0.5 && amount < 0) return
    if (zoomLevel > 2   && amount > 0) return

    // Zoom out.
    xform.appendScale(1/zoomLevel, 1/zoomLevel, center.x, center.y)

    // Figure out the zoom factor and zoom back in.
    xform.appendScale(tgtZoomLevel, tgtZoomLevel, center.x, center.y)

    graphicsContext2D.setTransform(xform)
  }
}

object GraphCanvas {
  type DrawAction = GraphicsContext => ()
}
