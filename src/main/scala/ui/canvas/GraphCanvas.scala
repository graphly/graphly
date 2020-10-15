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

  private var mode: EditingMode.State = Default.default

  private var selected: Option[Node] = None
  private def selected_=(node: Node): Unit = {
    selected.foreach(_.selected = false)
    node.selected = true
    selected = Some(node)
  }

  onMouseClicked = (e: MouseEvent) => {
    mode match {
      case _: EditingMode.Node =>
        val node = Node(e.getX, e.getY)
        shapes.prepend(node)
        selected = node
      case EditingMode.Edge => hitTest(e.getX, e.getY) foreach { start =>
        selected = start
        mode = EditingMode.DrawingEdge(start)
      }
      case EditingMode.DrawingEdge(start) =>
        hitTest(e.getX, e.getY) foreach { end =>
            shapes.prepend(Edge(start, end))
            selected = end
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
    shapes.foreach(_.draw(graphicsContext2D))
  }

  def hitTest(x: Double, y: Double): Option[Node] =
    shapes collectFirst { case node: Node if node.hitTest(x, y) => node }

  def drawingModeNodes(): Unit =
    mode = EditingMode.Source

  def drawingModeEdges(): Unit =
    mode = EditingMode.Edge
}

object GraphCanvas {
  object EditingMode {
    sealed trait State

    case object Edge extends State
    case class DrawingEdge(previous: ui.canvas.Node) extends State

    sealed trait Node extends State

    sealed trait InsertNode extends Node
    case object Source extends Node
    case object Sink extends Node
    case object Fork extends Node
    case object Join extends Node

    implicit val default: Default[State] = Source
  }
}