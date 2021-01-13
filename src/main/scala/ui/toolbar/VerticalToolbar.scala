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

  val itemSelected = new Event[GraphCanvasController.EditingMode.State]

  private val allButtonsTg   = new ToggleGroup()
  private val selectBtn      =
    new ToolbarButton("Select Nodes or Edges", "/assets/icons/select.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GraphCanvasController.EditingMode.Selecting)
      }
    }
  private val sourceBtn      =
    new ToolbarButton("Create Source Nodes", "/assets/icons/source.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Source])
        )
      }
    }
  private val forkBtn        =
    new ToolbarButton("Create Fork Nodes", "/assets/icons/fork.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Fork])
        )
      }
    }
  private val joinBtn        =
    new ToolbarButton("Create Join Nodes", "/assets/icons/join.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Join])
        )
      }
    }
  private val queueBtn       = new ToolbarButton(
    "Create Server (queue) Nodes",
    "/assets/icons/server.svg"
  ) {
    toggleGroup = allButtonsTg
    onAction = e => {
      itemSelected.dispatch(
        GraphCanvasController.EditingMode.Node(() => Default.default[Server])
      )
    }
  }
  private val sinkBtn        =
    new ToolbarButton("Create Sink Nodes", "/assets/icons/sink.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Sink])
        )
      }
    }
  private val terminalBtn    =
    new ToolbarButton("Create Terminal Nodes", "/assets/icons/terminal.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GraphCanvasController.EditingMode.Node(
          () => Default.default[Terminal]
        ))
      }
    }
  private val routerBtn      =
    new ToolbarButton("Create Router Nodes", "/assets/icons/router.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Router])
        )
      }
    }
  private val delayBtn       =
    new ToolbarButton("Create Delay Nodes", "/assets/icons/delay.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Delay])
        )
      }
    }
  private val loggerBtn      =
    new ToolbarButton("Create Logger Nodes", "/assets/icons/logger.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Logger])
        )
      }
    }
  private val classSwitchBtn = new ToolbarButton(
    "Create Class Switch Nodes",
    "/assets/icons/class-switch.svg"
  ) {
    toggleGroup = allButtonsTg
    onAction = e => {
      itemSelected.dispatch(GraphCanvasController.EditingMode.Node(
        () => Default.default[ClassSwitch]
      ))
    }
  }
  private val semaphoreBtn   =
    new ToolbarButton("Create Semaphore Nodes", "/assets/icons/semaphore.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GraphCanvasController.EditingMode.Node(
          () => Default.default[Semaphore]
        ))
      }
    }
  private val scalarBtn      =
    new ToolbarButton("Create Scalar Nodes", "/assets/icons/scalar.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Scalar])
        )
      }
    }
  private val placeBtn       =
    new ToolbarButton("Create Place Nodes", "/assets/icons/place.svg") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(
          GraphCanvasController.EditingMode.Node(() => Default.default[Place])
        )
      }
    }
  private val transitionBtn  = new ToolbarButton(
    "Create Transition Nodes",
    "/assets/icons/transition.svg"
  ) {
    toggleGroup = allButtonsTg
    onAction = e => {
      itemSelected.dispatch(GraphCanvasController.EditingMode.Node(
        () => Default.default[Transition]
      ))
    }
  }
  private val edgesBtn       =
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
    routerBtn,
    delayBtn,
    loggerBtn,
    classSwitchBtn,
    semaphoreBtn,
    scalarBtn,
    placeBtn,
    transitionBtn,
    new Separator(),
    edgesBtn
  )

  def controllerUpdatedMode(
      mode: GraphCanvasController.EditingMode.State
  ): Unit                    = {
    mode match {
      case GraphCanvasController.EditingMode.Selecting =>
        selectBtn.selected = true
      case _ =>
    }
  }
}
