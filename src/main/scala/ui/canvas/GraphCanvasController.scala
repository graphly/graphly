package ui.canvas

import model.sim.Sim
import ui.canvas.GraphCanvasController.EditingMode
import ui.canvas.GraphCanvasController.EditingMode.{EntryState, default}
import util.Default

import scala.collection.mutable

class GraphCanvasController(val view: GraphCanvas, val model: Sim) {
  // Bind this controller to that view.
  view.controller = this

  // Collection of all shapes that will be drawn. This may be later split
  // into nodes and edges since edges need to be drawn before the nodes.
  private val shapes = mutable.ArrayDeque.empty[Shape]

  // What state is the view in - whether we are creating nodes and connections,
  // moving objects, etc.
  private var viewMode: EditingMode.State = Default.default

  // Reference to currently selected object. Only one object can
  // be selected at a time. This may later change.
  private var selected: Option[Node] = None
  private def selected_=(node: Node): Unit = {
    selected.foreach(_.selected = false)
    node.selected = true
    selected = Some(node)
  }

  def setDrawMode(newState: EntryState): Unit = {
    selected match {
      case _: Some[Node] => selected.get.selected = false
      case _ =>
    }
    selected = None

    viewMode = newState
  }

  /**
   * Callback when we click inside the canvas.
   *
   * @param x : Logical X coordinate.
   * @param y : Logical Y coordinate.
   */
  def onMouseClick(x: Double, y: Double): Unit = {
    viewMode match {
      case _: EditingMode.Node =>
        val node = Node(x, y)
        shapes.prepend(node)
      case EditingMode.Edge => hitTest(x, y) foreach { start =>
        selected = start
        viewMode = EditingMode.DrawingEdge(start)
      }
      case EditingMode.DrawingEdge(start) =>
        hitTest(x, y) foreach { end =>
          // Deselect current node.
          selected.get.selected = false
          selected = None

          shapes.prepend(Edge(start, end))
          viewMode = EditingMode.Edge
        }
    }

    view.redraw(shapes)
  }

  private def hitTest(x: Double, y: Double): Option[Node] =
    shapes collectFirst { case node: Node if node.hitTest(x, y) => node }
}


object GraphCanvasController {
  object EditingMode {
    sealed trait State
    sealed trait EntryState extends State

    case object Edge extends EntryState
    case class DrawingEdge(previous: ui.canvas.Node) extends State

    sealed trait Node extends EntryState

    sealed trait InsertNode extends Node
    case object Source extends Node
    case object Sink extends Node
    case object Fork extends Node
    case object Join extends Node

    implicit val default: Default[State] = Source
  }
}