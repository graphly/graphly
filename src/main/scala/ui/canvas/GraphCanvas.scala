package ui.canvas

import javafx.event.EventHandler
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType

class GraphCanvas extends Canvas {
  private val gctx = graphicsContext2D
  private var nodes = List[Shape]()

  onMouseClicked = (e: MouseEvent) => {
    nodes ::= new Node(e.getX, e.getY)
    redraw()
  }

  onMouseMoved = (e: MouseEvent) => {
  }

  def redraw() = {
    gctx.fill = Color.White
    gctx.fillRect(0, 0, width.value, height.value)
    for (node <- nodes) node.Draw(gctx)
  }
}
