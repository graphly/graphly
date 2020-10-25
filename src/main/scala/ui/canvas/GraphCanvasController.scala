package ui.canvas

import java.io.{File, PrintWriter}

import io.Implicit._
import io.XMLSimRepresentation.Implicit._
import model.sim
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.stage.{FileChooser, Stage}
import ui.Controller
import ui.Position.Implicit.MouseEventPosition
import ui.canvas.Draw.Implicit.{ShapeConnection, ShapeNode}
import ui.canvas.GraphCanvasController.EditingMode
import ui.canvas.GraphCanvasController.EditingMode.default
import util.{Default, Event}

import scala.collection.{View, immutable, mutable}
import scala.reflect.ClassTag

class GraphCanvasController[D](val model: sim.Sim)(implicit val drawable: Draw[D]) extends Controller[Iterable[D] => Unit] {
  // Callbacks to run when we switch the mode
  val onSwitchMode = new Event[EditingMode.State]

  // What state is the view in - whether we are creating nodes and connections,
  // moving objects, etc.
  private var _mode: EditingMode.State = Default.default

  private def mode_=(state: EditingMode.State): Unit = {
    _mode = state

    // Call update callback when we change to an exposed state
    onSwitchMode.dispatch(state)
  }

  @inline
  final def mode: EditingMode.State = _mode

  final def switchMode(state: EditingMode.State, update: Iterable[D] => Unit): Unit = {
    mode = state
    update(drawables)
  }

  def drawables: View[D] = model.connections.view.map(c => drawable.connection(c, mode.highlights(c))) ++
    model.nodes.view.map(n => drawable.node(n, mode.highlights(n)))


  /**
   * Callback when we click inside the canvas.
   *
   * @param event  : MouseEvent for position and modifiers
   * @param update : Display update function
   */
  override def onMouseClick(event: MouseEvent, update: Iterable[D] => Unit): Unit = {
    val position = event.position.model
    mode match {
      case _: EditingMode.Node =>
        hitShape(position) match {
          case None =>
            val simNode = sim.Source(position)
            //            val node = Node(position, simNode)
            model.nodes += simNode
          //            shapes.prepend(node)
          case Some(node: sim.Node) =>
            mode = EditingMode.SelectNode(Set(node))
          case Some(edge: sim.Connection) =>
            mode = EditingMode.SelectEdge(Set(edge))
        }
      case EditingMode.BeginEdge => hitNode(position) match {
        case Some(start) => mode = EditingMode.DrawingEdge(start)
        case None => mode = EditingMode.Selecting
      }
      case EditingMode.DrawingEdge(start) =>
        hitNode(position) match {
          case Some(end) =>
            model.connections += sim.Connection(start, end)
            mode = EditingMode.DrawingEdge(end)
          case None =>
            mode = EditingMode.BeginEdge
        }
      case EditingMode.DragNode(nodes, _) =>
        mode = EditingMode.SelectNode(nodes)
      case boxSelect: EditingMode.BoxSelect =>
        // Currently inefficient, may be made more efficient upon refactor
        mode = EditingMode.SelectNode((boxSelect.active.toSet | boxSelect.prev) -- (boxSelect.active & boxSelect.prev))
      case select: EditingMode.Select =>
        hitShape(position) match {
          case Some(node: sim.Node) =>
            select match {
              case EditingMode.SelectNode(nodes) if event.shiftDown =>
                mode = EditingMode.SelectNode(if (nodes contains node) nodes - node else nodes + node)
              case _ =>
                mode = EditingMode.SelectNode(Set(node))
            }
          case Some(edge: sim.Connection) =>
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

    update(drawables)
  }

  override def onMouseDragged(event: MouseEvent, update: Iterable[D] => Unit): Unit = {
    val position = event.position.model
    mode match {
      case EditingMode.DragNode(nodes, from) =>
        nodes.foreach { node =>
          node.position += event.position.model - from
        }
        mode = EditingMode.DragNode(nodes, event.position.model)
      case EditingMode.SelectNode(nodes) if nodes.exists(_.hits(position)) =>
        mode = EditingMode.DragNode(nodes, position)
      case EditingMode.SelectNode(nodes) if event.shiftDown =>
        mode = EditingMode.BoxSelect(position, prev = nodes)
      case boxSelect@EditingMode.BoxSelect(origin) =>
        boxSelect.removeAll(_.position.inRectangle(origin, position).unary_!)
        boxSelect ++= model.nodes.view.filter {
          _.position.inRectangle(origin, position)
        }
        update(drawables ++ View(drawable.selectionBox(origin, position)))
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
    update(drawables)
  }

  override def onKeyTyped(event: KeyEvent, state: Iterable[D] => Unit): Unit = {
    mode match {
      case active: EditingMode.SelectActive[_] =>
        event.code match {
          case KeyCode.Undefined => // ScalaFX not recognising `delete` on local runtime
            active match {
              case active: EditingMode.SelectActiveNode =>
                model.nodes --= active.active
                model.connections.filterInPlace { connection =>
                  !active.active(connection.source) && !active.active(connection.target)
                }
              case EditingMode.SelectEdge(edges) =>
                model.connections --= edges
            }
            mode = EditingMode.Selecting
            state(drawables)
          case _ =>
        }
      case _ =>
    }
  }

  private def hitShape(hit: sim.Position): Option[sim.Shape] = hitNode(hit) orElse hitConnection(hit)

  private def hitNode(hit: sim.Position): Option[sim.Node] = {
    model.nodes.find(_.hits(hit))
  }

  private def hitConnection(hit: sim.Position): Option[sim.Connection] = {
    model.connections.find(_.hits(hit))
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
      var toolbarStatusMnemonic: String = toString

      def highlights(shape: sim.Shape): Boolean = false
    }

    sealed trait Entry extends State

    sealed trait Edge extends State {
      toolbarStatusMnemonic = "Creating edges"
    }

    case object BeginEdge extends Edge with Entry

    case class DrawingEdge(from: sim.Node) extends Edge {
      override def highlights(shape: sim.Shape): Boolean = shape == from
    }

    sealed trait Node extends Entry

    case object Source extends Node

    case object Sink extends Node

    case object Fork extends Node

    case object Join extends Node

    sealed trait Select extends State

    case object Selecting extends Select with Entry

    sealed abstract class SelectActive[T: ClassTag] extends Select {
      toolbarStatusMnemonic = "Selecting"
      val active: collection.Set[T]

      override def highlights(shape: sim.Shape): Boolean = {
        shape match {
          case shape: T =>
            active contains shape
          case _ =>
            false
        }
      }
    }

    sealed abstract class SelectActiveNode extends SelectActive[sim.Node]

    /* This class is not immutable for efficiency reasons, although this means you have to play nice with it
       We should probably refactor more of the immutability out for efficiency, or stick to it, rather than the mix
     */
    class BoxSelect(val origin: sim.Position, val prev: immutable.Set[sim.Node] = Set.empty) extends SelectActiveNode {
      private val _nodes: mutable.Set[sim.Node] = mutable.Set.empty
      override val active: collection.Set[sim.Node] = _nodes

      def removeAll(p: sim.Node => Boolean): Unit = {
        _nodes.filterInPlace(p andThen (!_))
      }

      def ++=(iterable: Iterable[sim.Node]): Unit = {
        _nodes ++= iterable
      }
    }

    object BoxSelect {
      def apply(origin: sim.Position, prev: immutable.Set[sim.Node] = Set.empty) = new BoxSelect(origin, prev)

      def unapply(boxSelect: BoxSelect): Option[sim.Position] = Some(boxSelect.origin)
    }

    case class SelectNode(override val active: immutable.Set[sim.Node]) extends SelectActiveNode

    case class DragNode(override val active: immutable.Set[sim.Node], from: sim.Position) extends SelectActiveNode

    case class SelectEdge(override val active: immutable.Set[sim.Connection]) extends SelectActive[sim.Connection]

    implicit val default: Default[State] = Selecting
  }

}