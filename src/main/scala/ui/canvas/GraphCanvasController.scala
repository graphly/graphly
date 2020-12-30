package ui.canvas

import java.io.{File, FileInputStream, PrintWriter}

import io.Implicit._
import io.XMLSimRepresentation.Implicit._
import model.sim.Trace.Image
import model.sim.{Connection, Node}
import model.sim.Shape.Metadata
import model.sim.Trace.Image
import model.{Position, sim}
import scalafx.scene.input.{Clipboard, ClipboardContent, KeyEvent, MouseButton, MouseEvent, ScrollEvent}
import scalafx.stage.{FileChooser, Stage}
import ui.Position.Implicit.MouseEventPosition
import ui.canvas.Draw.Implicit.DrawShape
import ui.canvas.GraphCanvasController.EditingMode.default
import ui.canvas.GraphCanvasController.{EditingMode, Redraw}
import ui.{Controller, GPosEvent, history}
import ui.util.Event
import util.Default

import scala.collection.{View, immutable, mutable}
import scala.reflect.{ClassTag, classTag}

class GraphCanvasController[D](var model: sim.Sim)(implicit
    val draw: Draw[D, sim.Shape]
) extends Controller[Redraw[D]] {
  // Callbacks to run when we switch the mode
  val onSwitchMode      = new Event[EditingMode.State]
  val onCanvasTransform = new Event[(Option[Position],
                                     Option[(Position, Double)]
                                    )]
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

  private val timeline = new history.Timeline

  override def onMousePress(gevt: GPosEvent[MouseEvent], update: Redraw[D]): Unit   = {
    val event: MouseEvent = gevt.evt

    super.onMousePress(gevt, update)
    val position = event.position.model

    // So far we drag only on when we press middle mouse.
    if (event.button == MouseButton.Middle) {
      mode = EditingMode.NavigationPan(gevt.screenPosition, mode)
      return
    }

    mode match {
      case traceMode: EditingMode.Trace =>
        hitTrace(position) match {
          // Clicked on a trace. What happens depends on the state we're in.
          case Some(trace: sim.Trace) => traceMode match {
              // Nothing is selected yet -> move to selected state with the trace we clicked on.
              case EditingMode.SelectingTrace =>
                mode = EditingMode.DragTrace(Set(trace), position, position)
              // We have some traces selected. If we Shift+clicked then switch trace's selected status.
              // Then start dragging all selected traces.
              case EditingMode.SelectTrace(selectedTraces) =>
                if (
                  (selectedTraces contains trace) &&
                  position
                    .inRectangle(trace.`end` - Position(50, 50), trace.`end`)
                ) {
                  mode = EditingMode
                    .ResizeTrace(selectedTraces, position, trace, position)
                } else {
                  val newSelectionSet =
                    if (event.isShiftDown)
                      if (selectedTraces contains trace) selectedTraces - trace
                      else selectedTraces + trace
                    else Set(trace)
                  mode =
                    EditingMode.DragTrace(newSelectionSet, position, position)
                }
              // Any other state is absolutely invalid here and we should throw an exception.
              case _ =>
            }

          // Pressed a mouse button over an empty area -> deselect everything.
          case _ => mode = EditingMode.SelectingTrace
        }
        update(None, Some(background))

      // Other entry states are handled in onMouseClick. Since mouse click is handled after onMouseRelease,
      // I think we should stop using onMouseClick and transition to these two.
      case _ =>
    }
  }

  override def onMouseRelease(gevt: GPosEvent[MouseEvent], update: Redraw[D]): Unit = {
    val event = gevt.evt
    super.onMouseRelease(gevt, update)
    val position = event.position.model

    mode match {
      case EditingMode.NavigationPan(_, prevMode) =>
        redrawMode(prevMode, update)
        return

      case traceMode: EditingMode.Trace =>
        traceMode match {
          // Stopped dragging traces. Leave them selected.
          case EditingMode.DragTrace(traces, _, origin) =>
            timeline += history.Move(traces, position - origin)
            mode = EditingMode.SelectTrace(traces)
          case EditingMode.ResizeTrace(traces, position, base, origin) =>
            val Position(x, y) = position - origin
            timeline += history.ResizeTrace(
              traces,
              1 + (x / base.width),
              1 + (y / base.height)
            )
            mode = EditingMode.SelectTrace(traces)
          // Anything else then deselect all traces.
          case _ => mode = EditingMode.SelectingTrace
        }
        update(None, Some(background))
        return

      case EditingMode.Node(mkNode) => hitShape(position) match {
          // If we clicked on nothing, make a new node.
          case None =>
            counters(mkNode) += 1
            val node = mkNode(
              mutable.Map.empty,
              s"$mkNode ${counters(mkNode)}",
              position
            )
            timeline(history.Add.node(Set(node)), model)
          // We clicked on a node, select it.
          case Some(node: sim.Node) => mode = EditingMode.SelectNode(Set(node))
          // We clicked on an edge, select it.
          // TODO: If ALT is pressed insert an elbow (meaningless node to tidy up the graph)?
          case Some(edge: sim.Connection) =>
            mode = EditingMode.SelectEdge(Set(edge))
        }

      // Finished dragging nodes.
      case EditingMode.DragNode(nodes, _, origin) =>
        timeline += history.Move(nodes, position - origin)
        mode = EditingMode.SelectNode(nodes)

      // Finished box selecting.
      case boxSelect: EditingMode.BoxSelect =>
        // Currently inefficient, may be made more efficient upon refactor
        mode = EditingMode.SelectNode(
          (boxSelect.active.toSet | boxSelect.prev) --
            (boxSelect.active & boxSelect.prev)
        )

      case select: EditingMode.Select => hitShape(position) match {
          // Clicked on a node -> update node selection set and selecting nodes.
          case Some(node: sim.Node) => select match {
              case EditingMode.SelectNode(nodes) if event.shiftDown =>
                mode = EditingMode.SelectNode(
                  if (nodes contains node) nodes - node else nodes + node
                )
              case _ if event.button == MouseButton.Secondary =>
                mode = EditingMode.FloatingMenu(node)
              case _ => mode = EditingMode.SelectNode(node)
            }
          // Clicked on an edge -> update edge selection set and selecting edges.
          case Some(edge: sim.Connection) => select match {
              case EditingMode.SelectEdge(edges) if event.shiftDown =>
                mode = EditingMode.SelectEdge(
                  if (edges contains edge) edges - edge else edges + edge
                )
              case _ => mode = EditingMode.SelectEdge(edge)
            }
          case _ => mode = EditingMode.Selecting
        }

      // Drawing edges - establishing start node.
      case EditingMode.BeginEdge => hitNode(position) match {
          case Some(start) => mode = EditingMode.DrawingEdge(start)
          case None => mode = EditingMode.Selecting
        }

      // Drawing edges - start node is set, this is the end.
      case EditingMode.DrawingEdge(start) => hitNode(position) match {
          case Some(end) =>
            timeline(history.Add.edge(Set(sim.Connection(start, end))), model)
            mode = EditingMode.DrawingEdge(end)
          case None => mode = EditingMode.BeginEdge
        }

      // Other states are invalid when we release mouse.
      case _ =>
    }

    update(Some(foreground), None)
  }

  override def onMouseDragged(gevt: GPosEvent[MouseEvent], update: Redraw[D]): Unit = {
    val event = gevt.evt
    val position = gevt.position

    mode match {
      case EditingMode.NavigationPan(prevPos, prevMode) =>
        val scrPos = gevt.screenPosition
        onCanvasTransform.dispatch(Some(scrPos - prevPos), None)
        redrawMode(EditingMode.NavigationPan(scrPos, prevMode), update)
        return
      case EditingMode.DragNode(nodes, from, origin) =>
        nodes.foreach { node => node.position += position - from }
        mode = EditingMode.DragNode(nodes, position, origin)
      case EditingMode.SelectNode(nodes) if nodes.exists(_.hits[D](position)) =>
        mode = EditingMode.DragNode(nodes, position, position)
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
          case Some(node) =>
            mode = EditingMode.DragNode(Set(node), position, position)
          case None => mode = EditingMode.BoxSelect(position)
        }
      case trace: EditingMode.Trace =>
        trace match {
          case EditingMode.DragTrace(traces, from, origin) =>
            traces.foreach { trace =>
              trace.position += event.position.model - from
            }
            mode = EditingMode.DragTrace(traces, position, origin)
          case EditingMode.ResizeTrace(traces, start, base, origin) =>
            traces.foreach { trace =>
              trace.width *= 1 - (start.x - position.x) / base.width
              trace.height *= 1 - (start.y - position.y) / base.height
            }
            mode = EditingMode.ResizeTrace(traces, position, base, origin)
          case _ =>
        }
        update(None, Some(background))
        return
      case _ => return
    }

    update(Some(foreground), None)
  }

  override def onScroll(gevt: GPosEvent[ScrollEvent], update: Redraw[D]): Unit = {
    val event = gevt.evt
    super.onScroll(gevt, update)

    val oScale = Some(gevt.position, event.getDeltaY / 400)
    onCanvasTransform.dispatch(None, oScale)

    redrawMode(mode, update)
  }

  private def modelToString(model: sim.Sim): String                       = {
    val header =
      "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>"
    s"$header\n${model.toRepresentation.toString}"
  }

  def save(): Unit                                                        = {
    val fileChooser: scalafx.stage.FileChooser = new FileChooser
    fileChooser.initialDirectory = new File(System.getProperty("user.home"))
    fileChooser.title = "Save Simulation"
    fileChooser.extensionFilters
      .add(new FileChooser.ExtensionFilter("JSIMgraph XML", "*.jsimg"))
    fileChooser.initialFileName = ".jsimg"
    Option(fileChooser.showSaveDialog(new Stage)).foreach { dest =>
      dest.createNewFile()
      new PrintWriter(dest) {
        write(modelToString(model))
        close()
      }
    }
  }

  def loadTrace(update: Redraw[D]): Unit                                  = {
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
      val trace = sim
        .Trace(image, Position.Zero, height = image.height, width = image.width)
      model.traces += trace
      redrawMode(EditingMode.SelectTrace(trace), update)
    }
  }

  def deleteSelected(update: Redraw[D]): Unit                             = {
    mode match {
      case active: EditingMode.SelectActiveNode =>
        timeline(
          history.Delete.node(active.active) +
            history.Delete.edge(model.connections.view.filter { connection =>
              !active.active(connection.source) &&
              !active.active(connection.target)
            }.toSet),
          model
        )
      case EditingMode.SelectEdge(edges) => model.connections --= edges
      case trace: EditingMode.ActiveTrace =>
        timeline(history.Delete.trace(trace.active), model)
        mode = EditingMode.SelectingTrace
        update(None, Some(background))
        return
      case _ =>
    }
    mode = EditingMode.Selecting
    update(Some(foreground), None)
  }

  private def putModelToClipboard(model: sim.Sim): Unit = {
    val content = new ClipboardContent()
    content.putString(modelToString(model))
    Clipboard.systemClipboard.content = content
  }

  private def getEdgesWithBothEndpoints(
      nodes: Set[Node]
  ): mutable.Set[Connection]                            =
    model.connections.view
      .filter(c => nodes.contains(c.source) && nodes.contains(c.target))
      .to(mutable.Set)

  private def modelFromSelectedNodes(nodes: Set[Node]): sim.Sim = {
    val simNodes = nodes.to(mutable.Set)
    val simEdges = getEdgesWithBothEndpoints(nodes)

    new sim.Sim(
      simNodes,            // nodes
      simEdges,            // connections
      mutable.Set.empty,   // classes
      mutable.Set.empty,   // measures
      mutable.Buffer.empty // traces
    )
  }

  def copySelectedNodes(update: Redraw[D]): Unit                = {
    mode match {
      case EditingMode.SelectNode(nodes) => putModelToClipboard(
          modelFromSelectedNodes(nodes)
        )
      case _ => println("Finish selecting nodes to copy them")
    }
  }

  def cutSelectedNodes(update: Redraw[D]): Unit = {
    mode match {
      case EditingMode.SelectNode(nodes) =>
        putModelToClipboard(modelFromSelectedNodes(nodes))
        deleteSelected(update)
      case _ =>
    }
  }

  def pasteSelectedNodes(update: Redraw[D]): Unit = {
    val content     = Clipboard.systemClipboard.content
    val pastedModel = xml.XML.loadString(content.getString).toSim
    timeline(
      history.Add.edge(pastedModel.connections) +
        history.Add.node(pastedModel.nodes) +
        history.Add.trace(pastedModel.traces.toSet),
      model
    )
    mode = EditingMode.SelectNode(pastedModel.nodes.toSet)

    update(Some(foreground), Some(background))
  }

  def undo(update: Redraw[D]): Unit               = {
    if (timeline.undo(model)) { update(Some(foreground), Some(background)) }
  }

  def redo(update: Redraw[D]): Unit = {
    if (timeline.redo(model)) { update(Some(foreground), Some(background)) }
  }

  private def hitShape(hit: Position): Option[sim.Element] =
    hitNode(hit) orElse hitConnection(hit)

  private def hitNode(hit: Position): Option[sim.Node] =
    model.nodes.find(_.hits(hit))

  private def hitConnection(hit: Position): Option[sim.Connection] =
    model.connections.find(_.hits[D](hit))

  private def hitTrace(hit: Position): Option[sim.Trace] =
    model.traces.findLast(_.hits(hit))
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

    case class FloatingMenu(override val active: immutable.Set[sim.Node])
        extends SelectActiveNode

    object FloatingMenu          {
      def apply(shapes: immutable.Set[sim.Node]): Select =
        if (shapes.isEmpty) Selecting else new FloatingMenu(shapes)

      def apply(shapes: sim.Node*): FloatingMenu =
        new FloatingMenu(shapes.toSet)
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
        last: Position,
        origin: Position
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
        last: Position,
        origin: Position
    ) extends ActiveTrace

    case class ResizeTrace(
        override val active: immutable.Set[sim.Trace],
        from: Position,
        base: sim.Trace,
        origin: Position
    ) extends ActiveTrace

    case class NavigationPan(origin: Position, prev: State)
      extends Entry {
      override def toolbarStatusMnemonic: String =
        s"Panning (will return to ${prev.toolbarStatusMnemonic})"
    }
  }
}
