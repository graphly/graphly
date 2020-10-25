package ui.canvas

import scalafx.scene.layout.Pane
import ui.util.Background
import ui.util.Property.Implicit._

class GraphCanvasContainer(val controller: GraphCanvasController[GraphCanvas.DrawAction]) extends Pane {
  val canvas = new GraphCanvas(controller)
  canvas.width <== this.width
  canvas.height <== this.height
  background <== canvas.fill map (Background(_))

  children = List(canvas)
}
