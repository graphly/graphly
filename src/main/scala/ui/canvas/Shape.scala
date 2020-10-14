package ui.canvas

import scalafx.scene.canvas.GraphicsContext

trait Shape {
  def Draw(gc: GraphicsContext)
}
