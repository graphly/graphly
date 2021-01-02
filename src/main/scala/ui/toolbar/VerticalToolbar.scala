package ui.toolbar

import model.sim._
import model.sim.defaults.Implicit._
import scalafx.geometry.Orientation
import scalafx.scene.control.{Separator, ToggleGroup, ToolBar}
import ui.canvas.GraphCanvasController
import ui.util.Event
import util.Default

class VerticalToolbar extends ToolBar {
  orientation = Orientation.Vertical

  // TODO Refactor state machine so that State -> EntryState.
  val itemSelected = new Event[GraphCanvasController.EditingMode.State]

  private val allButtonsTg = new ToggleGroup()
  private val selectBtn    =
    new ToolbarButton("Select Nodes or Edges", "/assets/icons/select.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GraphCanvasController.EditingMode.Selecting)
      }
    }
  private val sourceBtn    =
    new ToolbarButton("Create Source Nodes", "/assets/icons/source.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(Default.default[Source])
        )
      }
    }
  private val forkBtn      =
    new ToolbarButton("Create Fork Nodes", "/assets/icons/fork.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(Default.default[Fork])
        )
      }
    }
  private val joinBtn      =
    new ToolbarButton("Create Join Nodes", "/assets/icons/source.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(Default.default[Join])
        )
      }
    }
  private val queueBtn     =
    new ToolbarButton("Create Queue Nodes", "/assets/icons/queue.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(Default.default[Server])
        )
      }
    }
  private val sinkBtn      =
    new ToolbarButton("Create Sink Nodes", "/assets/icons/sink.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected
          .dispatch(GraphCanvasController.EditingMode.Node(Sink(SinkSection())))
      }
    }
  private val edgesBtn     =
    new ToolbarButton("Create Edges", "/assets/icons/edge.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GraphCanvasController.EditingMode.BeginEdge)
      }
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

  def controllerUpdatedMode(
      mode: GraphCanvasController.EditingMode.State
  ): Unit                  = {
    mode match {
      case GraphCanvasController.EditingMode.Selecting =>
        selectBtn.selected = true
      case _ =>
    }
  }
}
