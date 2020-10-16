package ui.canvas

import collection.Seq
import model.sim.{Sim, Connection}
import model.sim.Position.Implicits._
import model.sim
import ui.canvas.GraphCanvasController.EditingMode
import ui.canvas.GraphCanvasController.EditingMode.{EntryState, default}
import util.Default
import io.XMLSimRepresentation._
import io.Implicits._
import java.io.{File, PrintWriter}

import scala.collection.mutable

class GraphCanvasController(val model: Sim) {
  // Collection of all shapes that will be drawn. This may be later split
  // into nodes and edges since edges need to be drawn before the nodes.
  private val shapes = mutable.ArrayDeque.empty[Shape]

  // What state is the view in - whether we are creating nodes and connections,
  // moving objects, etc.
  private var viewMode: EditingMode.State = Default.default

  // Reference to currently selected object. Only one object can
  // be selected at a time. This may later change.
  private var _selected: Option[Node] = None

  @inline
  private final def selected: Option[Node] = _selected

  private def selected_=(node: Option[Node]): Unit = {
    selected.foreach(_.selected = false)
    node.foreach(_.selected = true)
    _selected = node
  }

  private def selected_=(node: Node): Unit = selected = Some(node)

  def setDrawMode(newState: EntryState): Unit = {
    selected = None
    viewMode = newState
  }

  /**
   * Callback when we click inside the canvas.
   *
   * @param x : Logical X coordinate.
   * @param y : Logical Y coordinate.
   */
  def onMouseClick(x: Double, y: Double, fnUpdate: Seq[Shape] => Unit): Unit = {
    viewMode match {
      case _: EditingMode.Node =>
        hitTest(x, y) match {
          case None =>
            val simNode = sim.Source((x, y))
            val node = Node(x, y, simNode)
            model.nodes += simNode
            shapes.prepend(node)
          case Some(node) =>
            viewMode = EditingMode.MovingNode
            selected = Some(node)
        }
      case EditingMode.MovingNode =>
        viewMode = Default.default
        selected = None
      case EditingMode.Edge => hitTest(x, y) foreach { start =>
        selected = start
        viewMode = EditingMode.DrawingEdge(start)
      }
      case EditingMode.DrawingEdge(start) =>
        hitTest(x, y) foreach { end =>
          // Deselect current node.
          selected = None

          shapes.prepend(Edge(start, end))
          model.connections += Connection(start.node, end.node)
          viewMode = EditingMode.Edge
        }
    }

    fnUpdate(shapes)
  }

  def onMouseMove(x: Double, y: Double, fnUpdate: Seq[Shape] => Unit): Unit = {
    viewMode match {
      case EditingMode.MovingNode =>
        selected match {
          case Some(node) =>
            node.x = x
            node.y = y
            fnUpdate(shapes)
          case _ =>
        }
      case _ =>
    }
  }

  private def hitTest(x: Double, y: Double): Option[Node] =
    shapes collectFirst { case node: Node if node.hitTest(x, y) => node }

  def save(): Unit = {
    val dest = new File(System.getProperty("user.home") + "/test.xml")
    dest.createNewFile()
    new PrintWriter(dest) {
      write(model.toRepresentation.toString)
      close()
    }
  }
}


object GraphCanvasController {

  object EditingMode {

    sealed trait State

    sealed trait EntryState extends State

    case object Edge extends EntryState

    case class DrawingEdge(previous: ui.canvas.Node) extends State

    case object MovingNode extends State

    sealed trait Node extends EntryState

    case object Source extends Node

    case object Sink extends Node

    case object Fork extends Node

    case object Join extends Node

    implicit val default: Default[State] = Source
  }

}