package ui.canvas

import javafx.event.EventHandler
import javafx.scene.input.MouseEvent
import scalafx.scene.canvas.Canvas

class GraphCanvas extends Canvas {
  private val gctx = graphicsContext2D

  onMouseClicked = (e: MouseEvent) => {
    println(s"${e.getX} ${e.getY} clicked")
    gctx.arc(e.getX, e.getY, 5, 5, 0, 360)
  }
}
