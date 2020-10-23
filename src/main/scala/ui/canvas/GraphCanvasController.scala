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

import scalafx.stage.{FileChooser, Stage}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.Includes._
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
      case EditingMode.BeginEdge => hitNode(position) match {
        case Some(start) => mode = EditingMode.DrawingEdge(start)
        case None => mode = EditingMode.Selecting
      }
      case EditingMode.DrawingEdge(start) =>
        hitNode(position) match {
          case Some(end) =>
            val simConnection = sim.Connection(start.node, end.node)
            shapes.prepend(Edge(start, end, simConnection))
            model.connections += Connection(start.node, end.node)
            mode = EditingMode.DrawingEdge(end)
          case None =>
            mode = EditingMode.BeginEdge
        }
      case EditingMode.DragNode(nodes, _) =>
        mode = EditingMode.SelectNode(nodes)
      case boxSelect: EditingMode.BoxSelect =>
        // Currently inefficient, may be made more efficient upon refactor
        mode = EditingMode.SelectNode((boxSelect.nodes.toSet | boxSelect.prev) -- (boxSelect.nodes & boxSelect.prev))
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
        nodes.foreach { node =>
          node.position += event.position - from
          node.node.position = node.position.model
        }
        mode = EditingMode.DragNode(nodes, event.position)
      case EditingMode.SelectNode(nodes) if nodes.exists(_.hitBy(position)) =>
        mode = EditingMode.DragNode(nodes, position)
      case EditingMode.SelectNode(nodes) if event.shiftDown =>
        mode = EditingMode.BoxSelect(position, prev = nodes)
      case boxSelect@EditingMode.BoxSelect(origin) =>
        boxSelect.removeAll(_.position.inRectangle(origin, position).unary_!)
        boxSelect ++= shapes.view.collect { case node: Node if node.position.inRectangle(origin, position) => node }
        update(shapes :+ new SelectionBox(origin, position))
        return
      case _: EditingMode.Select =>
        hitNode(position) match {
          case Some(node) =>
            mode = EditingMode.DragNode(Set(node), position)
          case None =>
            mode = EditingMode.BoxSelect(position)
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
            active match {
              case active: EditingMode.SelectActiveNode =>
                model.nodes --= active.nodes.view.map(_.node)
                val removed = shapes.collect {
                  case e: Edge if (active.nodes contains e.start) || (active.nodes contains e.end) => e
                }
                shapes --= removed
                model.connections --= removed.view.map(_.connection)
              case EditingMode.SelectEdge(edges) =>
                model.connections --= edges.view.map(_.connection)
            }
            mode = EditingMode.Selecting
            state(shapes)
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
    val fileChooser: scalafx.stage.FileChooser = new FileChooser
    fileChooser.initialDirectory = new File(System.getProperty("user.home"))
    fileChooser.title = "Save Simulation"
    fileChooser.extensionFilters.add(new FileChooser.ExtensionFilter("JSIMgraph XML", "*.jsimg"))
    fileChooser.initialFileName = ".jsimg"
    Option(fileChooser.showSaveDialog(new Stage)).foreach { dest =>
      dest.createNewFile()
      new PrintWriter(dest) {
        write(model.toRepresentation.toString)
        close()
      }
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

    sealed trait SelectActiveNode extends SelectActive {
      val nodes: collection.Set[ui.canvas.Node]
      override def shapes: View[ui.canvas.Shape] = nodes.view
    }

    /* This class is not immutable for efficiency reasons, although this means you have to play nice with it
       We should probably refactor more of the immutability out for efficiency, or stick to it, rather than the mix
     */
    class BoxSelect(val origin: Position, val prev: immutable.Set[ui.canvas.Node] = Set.empty) extends SelectActiveNode {
      private val _nodes: mutable.Set[ui.canvas.Node] = mutable.Set.empty
      override val nodes: collection.Set[ui.canvas.Node] = _nodes

      def removeAll(p: ui.canvas.Node => Boolean): Unit = {
        _nodes.filterInPlace(node => {
          if (p(node)) {
            // Impure, but avoids looping twice. _nodes is strict so this operation will work as expected
            node.highlight ^= true
            false
          } else true
        })
      }
      def ++=(iterable: Iterable[ui.canvas.Node]): Unit = {
        iterable.foreach {
          case node if !(_nodes contains node) =>
            node.highlight ^= true
            _nodes += node
          case _ =>
        }
      }

      override def start(): Unit = prev.foreach(_.highlight = true); nodes.foreach(_.highlight ^= true)
      override def end(): Unit = nodes.foreach(_.highlight = false); prev.foreach(_.highlight = false)
    }
    object BoxSelect {
      def apply(origin: Position, prev: immutable.Set[ui.canvas.Node] = Set.empty) = new BoxSelect(origin, prev)
      def unapply(boxSelect: BoxSelect): Option[Position] = Some(boxSelect.origin)
    }

    case class SelectNode(nodes: immutable.Set[ui.canvas.Node]) extends SelectActiveNode {
      override def shapes: View[ui.canvas.Shape] = nodes.view
    }
    case class DragNode(nodes: immutable.Set[ui.canvas.Node], from: Position) extends SelectActiveNode {
      override def shapes: View[ui.canvas.Shape] = nodes.view
    }

    case class SelectEdge(edges: immutable.Set[ui.canvas.Edge]) extends SelectActive {
      override def shapes: View[ui.canvas.Shape] = edges.view
    }

    implicit val default: Default[State] = Selecting
  }

}