package ui.canvas

import scalafx.scene.layout.Pane

class GraphCanvasContainer extends Pane {
  val canvas = new GraphCanvas
  canvas.width <== this.width
  canvas.height <== this.height

  children = List(canvas)
}
