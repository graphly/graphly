package ui.canvas

import collection.{Seq, View, immutable, mutable}
import model.sim.{Connection, Sim}
import model.sim.Position.Implicits._
import model.sim
import ui.canvas.GraphCanvasController.EditingMode
import ui.canvas.GraphCanvasController.EditingMode.{DragNode, Entry, default}
import util.Default
import io.XMLSimRepresentation._
import io.Implicits._
import java.io.{File, PrintWriter}

import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import ui.Position.Implicits.MouseEventPosition
import ui.{Controller, Position, canvas}

class GraphCanvasController(val model: Sim) extends Controller[Seq[Shape] => Unit] {
  // Collection of all shapes that will be drawn. This may be later split
  // into nodes and edges since edges need to be drawn before the nodes.
  private val shapes = mutable.ArrayDeque.empty[Shape]

  // What state is the view in - whether we are creating nodes and connections,
  // moving objects, etc.
  private var _mode: EditingMode.State = Default.default
  private def mode_=(state: EditingMode.State): Unit = {
    _mode.end()
    _mode = state
    state.start()
  }
  @inline
  final def mode: EditingMode.State = _mode

  final def switchMode(state: EditingMode.State, update: Seq[Shape] => Unit): Unit = {
    mode = state
    update(shapes)
  }

  /**
   * Callback when we click inside the canvas.
   *
   * @param event: MouseEvent for position and modifiers
   * @param update: Display update function
   */
  override def onMouseClick(event: MouseEvent, update: Seq[Shape] => Unit): Unit = {
    val position = event.position
    mode match {
      case _: EditingMode.Node =>
        hitShape(position) match {
          case None =>
            val simNode = sim.Source(position.model)
            val node = Node(position, simNode)
            model.nodes += simNode
            shapes.prepend(node)
          case Some(node: Node) =>
            mode = EditingMode.SelectNode(Set(node))
          case Some(edge: Edge) =>
            mode = EditingMode.SelectEdge(Set(edge))
        }
      case EditingMode.BeginEdge => hitNode(position) foreach { start =>
        mode = EditingMode.DrawingEdge(start)
      }
      case EditingMode.DrawingEdge(start) =>
        hitNode(position) match {
          case Some(end) =>
            shapes.prepend(Edge(start, end))
            model.connections += Connection(start.node, end.node)
            mode = EditingMode.DrawingEdge(end)
          case None =>
            mode = EditingMode.BeginEdge
        }
      case EditingMode.DragNode(nodes, _) =>
        mode = EditingMode.SelectNode(nodes)
      case select: EditingMode.Select =>
        hitShape(position) match {
          case Some(node: Node) =>
            select match {
              case EditingMode.SelectNode(nodes) if event.shiftDown =>
                mode = EditingMode.SelectNode(if (nodes contains node) nodes - node else nodes + node)
              case _ =>
                mode = EditingMode.SelectNode(Set(node))
            }
          case Some(edge: Edge) =>
            select match {
              case EditingMode.SelectEdge(edges) if event.shiftDown =>
                mode = EditingMode.SelectEdge(if (edges contains edge) edges - edge else edges + edge)
              case _ =>
                mode = EditingMode.SelectEdge(Set(edge))
            }
          case _ =>
            mode = EditingMode.Selecting
        }
    }

    update(shapes)
  }

  override def onMouseDragged(event: MouseEvent, update: Seq[Shape] => Unit): Unit = {
    val position = event.position
    mode match {
      case EditingMode.DragNode(nodes, from) =>
        nodes.foreach(_.position += event.position - from)
        mode = EditingMode.DragNode(nodes, event.position)
      case EditingMode.SelectNode(nodes) if nodes.exists(_.hitBy(position)) =>
        mode = EditingMode.DragNode(nodes, position)
      case _: EditingMode.Select =>
        hitShape(position) foreach {
          case node: Node =>
            mode = EditingMode.DragNode(Set(node), position)
          case _ =>
        }
      case _ => return
    }
    update(shapes)
  }

  override def onKeyTyped(event: KeyEvent, state: Seq[Shape] => Unit): Unit = {
    mode match {
      case active: EditingMode.SelectActive =>
        event.code match {
          case KeyCode.Undefined => // ScalaFX not recognising `delete` on local runtime
            shapes --= active.shapes
            mode = EditingMode.Selecting
            state(shapes) // TODO doesn't actually delete from model
        }
      case _ =>
    }
  }

  private def hitShape(hit: Position): Option[Shape] = {
    shapes find (_.hitBy(hit))
  }

  private def hitNode(hit: Position): Option[Node] = {
    shapes collectFirst { case node: Node if node.hitBy(hit) => node }
  }

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

    sealed trait State {
      def start(): Unit = ()
      def end(): Unit = ()
    }

    sealed trait Entry extends State

    sealed trait Edge extends State
    case object BeginEdge extends Edge with Entry
    case class DrawingEdge(from: ui.canvas.Node) extends Edge {
      override def start(): Unit = from.highlight = true
      override def end(): Unit = from.highlight = false
    }

    sealed trait Node extends Entry

    case object Source extends Node

    case object Sink extends Node

    case object Fork extends Node

    case object Join extends Node

    sealed trait Select extends State

    case object Selecting extends Select

    sealed trait SelectActive extends Select {
      def shapes: View[ui.canvas.Shape]
      override def start(): Unit = shapes.foreach(_.highlight = true)
      override def end(): Unit = shapes.foreach(_.highlight = false)
    }
    case class SelectNode(nodes: Set[ui.canvas.Node]) extends SelectActive {
      override def shapes: View[ui.canvas.Shape] = nodes.view
    }
    case class DragNode(nodes: Set[ui.canvas.Node], from: Position) extends SelectActive {
      override def shapes: View[ui.canvas.Shape] = nodes.view
    }

    case class SelectEdge(edges: Set[ui.canvas.Edge]) extends SelectActive {
      override def shapes: View[ui.canvas.Shape] = edges.view
    }

    implicit val default: Default[State] = Selecting
  }

}