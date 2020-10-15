package ui.canvas

import scalafx.scene.canvas.GraphicsContext

trait Shape {
  def draw(context: GraphicsContext)
}
