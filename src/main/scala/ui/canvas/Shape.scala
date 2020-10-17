package ui.canvas

import scalafx.scene.canvas.GraphicsContext
import ui.Position

trait Shape {
  var highlight: Boolean
  def draw(context: GraphicsContext): Unit
  def hitBy(position: Position): Boolean
}
