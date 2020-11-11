package ui.toolbar

import model.sim._
import scalafx.geometry.Orientation
import scalafx.scene.control.{Separator, ToggleGroup, ToolBar}
import ui.canvas.{GraphCanvasController => GCC}
import ui.util.Event

class VerticalToolbar extends ToolBar {
  orientation = Orientation.Vertical

  // TODO Refactor state machine so that State -> EntryState.
  val itemSelected = new Event[GCC.EditingMode.State]

  private val allButtonsTg                                     = new ToggleGroup()
  private val selectBtn                                        =
    new ToolbarButton("Select Nodes or Edges", "assets/icons/select-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.Selecting) }
    }
  private val sourceBtn                                        =
    new ToolbarButton("Create Source Nodes", "assets/icons/source-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.Node(Source)) }
    }
  private val forkBtn                                          =
    new ToolbarButton("Create Fork Nodes", "assets/icons/fork-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.Node(Fork)) }
    }
  private val joinBtn                                          =
    new ToolbarButton("Create Join Nodes", "assets/icons/source-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.Node(Join)) }
    }
  private val queueBtn                                         =
    new ToolbarButton("Create Queue Nodes", "assets/icons/queue-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.Node(Queue)) }
    }
  private val sinkBtn                                          =
    new ToolbarButton("Create Sink Nodes", "assets/icons/sink-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.Node(JobSink)) }
    }
  private val edgesBtn                                         =
    new ToolbarButton("Create Edges", "assets/icons/edge-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.BeginEdge) }
    }

  items = List(
    selectBtn,
    new Separator(),
    sourceBtn,
    forkBtn,
    joinBtn,
    queueBtn,
    sinkBtn,
    new Separator(),
    edgesBtn
  )
  /*
    new Separator(),
    new ToolbarButton("Add Trace", "assets/icons/trace-add-32.png")          {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.) }
    },
    new ToolbarButton("Select Trace", "assets/icons/select-32.png")          {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.SelectingTrace) }
    }
   */

  def controllerUpdatedMode(mode: GCC.EditingMode.State): Unit = {
    mode match {
      case GCC.EditingMode.Selecting => selectBtn.selected = true
      case _ =>
    }
  }
}
