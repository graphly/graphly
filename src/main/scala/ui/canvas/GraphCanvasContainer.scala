package ui.canvas

import scalafx.scene.layout.Pane

class GraphCanvasContainer(val controller: GraphCanvasController) extends Pane {
  val canvas = new GraphCanvas(controller)
  canvas.width <== this.width
  canvas.height <== this.height

  children = List(canvas)
}
