package ui.canvas

import javafx.scene.input.MouseEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color

import scala.collection.mutable

class GraphCanvas extends Canvas {
  var controller: GraphCanvasController = _

  private def screenToWorldTransform(x: Double, y: Double): (Double, Double) = (x, y)

  onMouseClicked = (e: MouseEvent) => {
    val (wx, wy) = screenToWorldTransform(e.getX, e.getY)
    controller.onMouseClick(wx, wy)
  }

//  onMouseMoved = (e: MouseEvent) => {
//    hitTest(e.getX, e.getY) match {
//      case Some(node) =>
//        if (startNode contains node) {
//          tempSelect = Some(node)
//          tempSelect.get.selected = true
//          redraw()
//        }
//      case None =>
//        if (tempSelect.isDefined && tempSelect != startNode) {
//          tempSelect.get.selected = false
//          tempSelect = None
//          redraw()
//        }
//    }
//  }

  def redraw(shapes: mutable.ArrayDeque[Shape]): Unit = {
    graphicsContext2D.fill = Color.White
    graphicsContext2D.fillRect(0, 0, width.value, height.value)

    shapes.foreach(_.draw(graphicsContext2D))
  }
}
