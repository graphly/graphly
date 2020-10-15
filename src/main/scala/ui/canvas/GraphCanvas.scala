package ui.canvas

import javafx.scene.input.MouseEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import util.Default

import GraphCanvas._
import GraphCanvas.EditingMode.default

import scala.collection.mutable

class GraphCanvas extends Canvas {
  private val shapes = mutable.ArrayDeque.empty[Shape]

  private var _mode: EditingMode.State = Default.default

  private def mode: EditingMode.State = _mode
  private def mode_=(state: EditingMode.State): EditingMode.State = {
    _mode.cleanUp()
    val old = _mode
    _mode = state
    old
  }

//  private var startNode: Option[Node] = None
  private var tempSelect: Option[Node] = None

  onMouseClicked = (e: MouseEvent) => {
    mode match {
      case _: EditingMode.Node => shapes.prepend(Node(e.getX, e.getY))
      case EditingMode.Edge => hitTest(e.getX, e.getY) foreach { start =>
        mode = EditingMode.DrawingEdge(start)
      }
      case EditingMode.DrawingEdge(start) =>
        hitTest(e.getX, e.getY) foreach { end =>
            shapes.prepend(Edge(start, end))
            end.selected = true
            mode = EditingMode.DrawingEdge(end)
        }
    }

    redraw()
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

  def redraw(): Unit = {
    graphicsContext2D.fill = Color.White
    graphicsContext2D.fillRect(0, 0, width.value, height.value)
    for (node <- shapes) node.draw(graphicsContext2D)
  }

  def hitTest(x: Double, y: Double): Option[Node] = shapes collectFirst { case node: Node if node.hitTest(x, y) => node }

  def drawingModeNodes(): Unit = {
    mode.cleanUp()
    mode = EditingMode.Source
    println("Drawing Nodes now")
  }

  def drawingModeEdges(): Unit = {
    mode = EditingMode.Edge
    println("Drawing Edges now")
  }
}

object GraphCanvas {
  object EditingMode {
    sealed trait State {
      def cleanUp(): Unit = ()

    }

    case object Edge extends State
    case class DrawingEdge(startNode: ui.canvas.Node) extends State {
      override def cleanUp(): Unit = {
        startNode.selected = false
      }
    }

    sealed trait Node extends State
    case object Source extends Node
    case object Sink extends Node
    case object Fork extends Node
    case object Join extends Node

    implicit val default: Default[State] = Source
  }
}