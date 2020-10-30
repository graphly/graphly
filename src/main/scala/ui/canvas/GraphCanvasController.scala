package ui.canvas

import java.io.{File, FileInputStream, PrintWriter}

import io.Implicit._
import io.XMLSimRepresentation.Implicit._
import model.sim.Trace.Image
import model.sim.Node
import model.sim.Shape.Metadata
import model.{Position, sim}
import scalafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import scalafx.stage.{FileChooser, Stage}
import ui.Controller
import ui.Position.Implicit.MouseEventPosition
import ui.canvas.Draw.Implicit.DrawShape
import ui.canvas.GraphCanvasController.EditingMode.default
import ui.canvas.GraphCanvasController.{EditingMode, Redraw}
import ui.util.Event
import util.Default

import scala.collection.{View, immutable, mutable}
import scala.reflect.{ClassTag, classTag}

class GraphCanvasController[D](val model: sim.Sim)(implicit
    val draw: Draw[D, sim.Shape]
) extends Controller[Redraw[D]] {
  // Callbacks to run when we switch the mode
  val onSwitchMode                                                             = new Event[EditingMode.State]
  private val counters: mutable.Map[(Metadata, String, Position) => Node, Int] =
    mutable.Map(
      sim.Source -> 0,
      sim.Queue -> 0,
      sim.Sink -> 0,
      sim.Fork -> 0,
      sim.Join -> 0
    )

  // What state is the view in - whether we are creating nodes and connections,
  // moving objects, etc.
  private var _mode: EditingMode.State = Default.default

  final def redrawMode(state: EditingMode.State, update: Redraw[D]): Unit = {
    mode = state
    update(Some(foreground), Some(background))
  }

  def foreground: View[D] =
    model.connections.view.map(c => draw.shape(c, mode.highlights(c))) ++
      model.nodes.view.map(n => draw.shape(n, mode.highlights(n)))

  def background: View[D] =
    model.traces.view.map(t => draw.shape(t, highlight = mode.highlights(t)))

  @inline
  final def mode: EditingMode.State = _mode

  private def mode_=(state: EditingMode.State): Unit = {
    _mode = state

    // Call update callback when we change to an exposed state
    onSwitchMode.dispatch(state)
  }

  /**
    * Callback when we click inside the canvas.
    *
    * @param event  : MouseEvent for position and modifiers
    * @param update : Display update function
    */
  override def onMouseClick(event: MouseEvent, update: Redraw[D]): Unit   = {
    val position = event.position.model
    mode match {
      case EditingMode.Node(mkNode) => hitShape(position) match {
          case None =>
            counters(mkNode) += 1
            val name = s"$mkNode ${counters(mkNode)}"
            model.nodes += mkNode(mutable.Map.empty, name, position)
          case Some(node: sim.Node) => mode = EditingMode.SelectNode(Set(node))
          case Some(edge: sim.Connection) =>
            mode = EditingMode.SelectEdge(Set(edge))
        }
      case EditingMode.BeginEdge => hitNode(position) match {
          case Some(start) => mode = EditingMode.DrawingEdge(start)
          case None => mode = EditingMode.Selecting
        }
      case EditingMode.DrawingEdge(start) => hitNode(position) match {
          case Some(end) =>
            model.connections += sim.Connection(start, end)
            mode = EditingMode.DrawingEdge(end)
          case None => mode = EditingMode.BeginEdge
        }
      case EditingMode.DragNode(nodes, _) =>
        mode = EditingMode.SelectNode(nodes)
      case boxSelect: EditingMode.BoxSelect =>
        // Currently inefficient, may be made more efficient upon refactor
        mode = EditingMode.SelectNode(
          (boxSelect.active.toSet | boxSelect.prev) --
            (boxSelect.active & boxSelect.prev)
        )
      case select: EditingMode.Select => hitShape(position) match {
          case Some(node: sim.Node) => select match {
              case EditingMode.SelectNode(nodes) if event.shiftDown =>
                mode = EditingMode.SelectNode(
                  if (nodes contains node) nodes - node else nodes + node
                )
              case _ => mode = EditingMode.SelectNode(node)
            }
          case Some(edge: sim.Connection) => select match {
              case EditingMode.SelectEdge(edges) if event.shiftDown =>
                mode = EditingMode.SelectEdge(
                  if (edges contains edge) edges - edge else edges + edge
                )
              case _ => mode = EditingMode.SelectEdge(edge)
            }
          case _ => mode = EditingMode.Selecting
        }
      case EditingMode.DragTrace(trace, _) =>
        mode = EditingMode.SelectTrace(trace)
      case select: EditingMode.Trace =>
        hitTrace(position) match {
          case Some(trace: sim.Trace) => select match {
              case EditingMode.SelectTrace(traces) if event.shiftDown =>
                mode = EditingMode.SelectTrace(
                  if (traces contains trace) traces - trace else traces + trace
                )
              case _ => mode = EditingMode.SelectTrace(trace)
            }
          case _ =>
            mode =
              if (mode == EditingMode.SelectingTrace) EditingMode.Selecting
              else EditingMode.SelectingTrace
        }
        update(None, Some(background))
        return
    }

    update(Some(foreground), None)
  }

  override def onMouseDragged(event: MouseEvent, update: Redraw[D]): Unit = {
    val position = event.position.model
    mode match {
      case EditingMode.DragNode(nodes, from) =>
        nodes.foreach { node => node.position += event.position.model - from }
        mode = EditingMode.DragNode(nodes, position)
      case EditingMode.SelectNode(nodes) if nodes.exists(_.hits[D](position)) =>
        mode = EditingMode.DragNode(nodes, position)
      case EditingMode.SelectNode(nodes) if event.shiftDown =>
        mode = EditingMode.BoxSelect(position, prev = nodes)
      case boxSelect @ EditingMode.BoxSelect(origin) =>
        boxSelect.removeAll(_.position.inRectangle(origin, position).unary_!)
        boxSelect ++= model.nodes.view.filter {
          _.position.inRectangle(origin, position)
        }

        update(
          Some(foreground ++ View(draw.selectionBox(origin, position))),
          None
        )
        return
      case _: EditingMode.Select => hitNode(position) match {
          case Some(node) => mode = EditingMode.DragNode(Set(node), position)
          case None => mode = EditingMode.BoxSelect(position)
        }
      case trace: EditingMode.Trace =>
        trace match {
          case EditingMode.DragTrace(traces, from) =>
            traces.foreach { trace =>
              trace.position += event.position.model - from
            }
            mode = EditingMode.DragTrace(traces, position)
          case EditingMode.SelectTrace(traces)
              if traces.exists(_.hits[D](position)) =>
            mode = EditingMode.DragTrace(traces, position)
          case _ => hitTrace(position) match {
              case Some(trace) =>
                mode = EditingMode.DragTrace(Set(trace), position)
              case _ =>
            }
        }
        update(None, Some(background))
        return
      case _ => return
    }

    update(Some(foreground), None)
  }

  override def onKeyTyped(event: KeyEvent, update: Redraw[D]): Unit       = {
    mode match {
      case active: EditingMode.Active[_] => event.code match {
          // ScalaFX not recognising `delete` on local runtime
          case KeyCode.Undefined =>
            active match {
              case active: EditingMode.SelectActiveNode =>
                model.nodes --= active.active
                model.connections.filterInPlace { connection =>
                  !active.active(connection.source) &&
                  !active.active(connection.target)
                }
              case EditingMode.SelectEdge(edges) => model.connections --= edges
              case trace: EditingMode.ActiveTrace =>
                // Unknown failure for `--=` to work
                model.traces.filterInPlace(!trace.active(_))
                update(None, Some(background))
                return
            }
            mode = EditingMode.Selecting
            update(Some(foreground), None)
          case _ =>
        }
      case _ =>
    }
  }

  def save(): Unit                                         = {
    val fileChooser: scalafx.stage.FileChooser = new FileChooser
    fileChooser.initialDirectory = new File(System.getProperty("user.home"))
    fileChooser.title = "Save Simulation"
    fileChooser.extensionFilters
      .add(new FileChooser.ExtensionFilter("JSIMgraph XML", "*.jsimg"))
    fileChooser.initialFileName = ".jsimg"
    Option(fileChooser.showSaveDialog(new Stage)).foreach { dest =>
      dest.createNewFile()
      new PrintWriter(dest) {
        write(
          "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>\n"
        )
        write(model.toRepresentation.toString)
        close()
      }
    }
  }

  def loadTrace(update: Redraw[D]): Unit                   = {
    val fileChooser: scalafx.stage.FileChooser = new FileChooser
    fileChooser.title = "Open Trace"
    fileChooser.initialDirectory = new File(System.getProperty("user.home"))
    fileChooser.extensionFilters.add(
      new FileChooser.ExtensionFilter("Portable Network Graphics", "*.png")
    )
    fileChooser.extensionFilters
      .add(new FileChooser.ExtensionFilter("JPEG", "*.jpg"))
    fileChooser.initialFileName = ".png"
    Option(fileChooser.showOpenDialog(new Stage)).foreach { dest =>
      val image = Image(new FileInputStream(dest))
      val trace = sim.Trace(image, Position.Zero, 750, 750)
      model.traces += trace
      redrawMode(EditingMode.SelectTrace(trace), update)
    }
  }

  private def hitShape(hit: Position): Option[sim.Element] =
    hitNode(hit) orElse hitConnection(hit)

  private def hitNode(hit: Position): Option[sim.Node] =
    model.nodes.find(_.hits[D](hit))

  private def hitConnection(hit: Position): Option[sim.Connection] =
    model.connections.find(x => x.hits[D](hit))

  private def hitTrace(hit: Position): Option[sim.Trace] =
    model.traces.find(x => x.hits[D](hit))
}

object GraphCanvasController      {
  type Redraw[D] = (Option[Iterable[D]], Option[Iterable[D]]) => Unit

  object EditingMode {

    sealed trait State {
      def toolbarStatusMnemonic: String = toString

      def highlights(shape: sim.Shape): Boolean = false
    }

    sealed trait Entry extends State

    sealed trait Edge     extends State {
      override def toolbarStatusMnemonic = "Create [Edge]"
    }

    case object BeginEdge extends Edge with Entry

    case class DrawingEdge(from: sim.Node) extends Edge {
      override def highlights(shape: sim.Shape): Boolean = shape == from
    }

    case class Node(
        constructor: (sim.Shape.Metadata, String, Position) => sim.Node
    ) extends Entry {
      override def toolbarStatusMnemonic = "Create [Node]"
    }

    sealed trait Select                    extends State

    case object Selecting extends Select with Entry

    sealed abstract class Active[T: ClassTag] extends State {
      val active: collection.Set[T]

      override def toolbarStatusMnemonic: String =
        s"Select [${classTag[T].runtimeClass.getSimpleName}:${active.size}]"

      override def highlights(shape: sim.Shape): Boolean = {
        shape match {
          case shape: T => active contains shape
          case _ => false
        }
      }
    }

    sealed abstract class SelectActiveNode extends Active[sim.Node] with Select

    case class SelectNode(override val active: immutable.Set[sim.Node])
        extends SelectActiveNode

    object SelectNode            {
      def apply(shapes: immutable.Set[sim.Node]): Select =
        if (shapes.isEmpty) Selecting else new SelectNode(shapes)

      def apply(shapes: sim.Node*): SelectNode = new SelectNode(shapes.toSet)
    }

    /* This class is not immutable for efficiency reasons, although this means you have to play nice with it
       We should probably refactor more of the immutability out for efficiency, or stick to it, rather than the mix
     */
    class BoxSelect(
        val origin: Position,
        val prev: immutable.Set[sim.Node] = Set.empty
    ) extends SelectActiveNode {
      private val _nodes: mutable.Set[sim.Node]     = mutable.Set.empty
      override val active: collection.Set[sim.Node] = _nodes

      override def toolbarStatusMnemonic: String = s"Select [Box]"

      def removeAll(p: sim.Node => Boolean): Unit = {
        _nodes.filterInPlace(p andThen (!_))
      }

      def ++=(iterable: Iterable[sim.Node]): Unit = { _nodes ++= iterable }
    }

    object BoxSelect             {
      def apply(origin: Position, prev: immutable.Set[sim.Node] = Set.empty) =
        new BoxSelect(origin, prev)

      def unapply(boxSelect: BoxSelect): Option[Position] =
        Some(boxSelect.origin)
    }

    case class DragNode(
        override val active: immutable.Set[sim.Node],
        from: Position
    ) extends SelectActiveNode

    case class SelectEdge(override val active: immutable.Set[sim.Connection])
        extends Active[sim.Connection]
        with Select

    object SelectEdge            {
      def apply(shapes: immutable.Set[sim.Connection]): Select =
        if (shapes.isEmpty) Selecting else new SelectEdge(shapes)

      def apply(shapes: sim.Connection*): SelectEdge =
        new SelectEdge(shapes.toSet)
    }

    implicit val default: Default[State] = Selecting

    sealed trait Trace extends State

    object SelectingTrace             extends Trace                        {
      override def toolbarStatusMnemonic: String = s"Trace"
    }

    sealed abstract class ActiveTrace extends Active[sim.Trace] with Trace {
      override def toolbarStatusMnemonic: String = s"Trace [${active.size}]"
    }

    case class SelectTrace(override val active: immutable.Set[sim.Trace])
        extends ActiveTrace {
      override def toolbarStatusMnemonic: String = s"Trace [${active.size}]"
    }

    object SelectTrace {
      def apply(trace: immutable.Set[sim.Trace]): Trace =
        if (trace.isEmpty) SelectingTrace else new SelectTrace(trace)

      def apply(trace: sim.Trace*): SelectTrace = new SelectTrace(trace.toSet)
    }

    case class DragTrace(
        override val active: immutable.Set[sim.Trace],
        from: Position
    ) extends ActiveTrace

  }
}
