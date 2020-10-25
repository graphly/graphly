package ui

import javafx.event.ActionEvent
import model.sim.Sim
import scalafx.scene.Scene
import scalafx.scene.control.{
  Label,
  Menu,
  MenuBar,
  MenuItem,
  SeparatorMenuItem,
  TextArea,
  ToolBar
}
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.BorderPane
import ui.canvas.{GraphCanvasContainer, GraphCanvasController}
import ui.canvas.GraphCanvas.DrawActionDraw

class AppMainSceneView(width: Double, height: Double)
    extends Scene(width, height) {
  private val model: Sim = Sim.empty
  private val controller = new GraphCanvasController(model)
  private val graphContainer = new GraphCanvasContainer(controller)

  private val statusBar = new Label() {
    text = s"Status: ${controller.mode.toolbarStatusMnemonic}"
  }
  controller.onSwitchMode += (state =>
    statusBar.text = s"Status: ${state.toolbarStatusMnemonic}"
  )

  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("File") {
          items = List(
            new MenuItem("Save") {
              onAction = (_: ActionEvent) => {
                controller.save()
              }
              accelerator =
                new KeyCodeCombination(KeyCode.S, KeyCombination.ControlDown)
            },
            new MenuItem("Save As") {
              disable = true
              accelerator = new KeyCodeCombination(
                KeyCode.S,
                KeyCombination.ControlDown,
                KeyCombination.AltDown
              )
            },
            new SeparatorMenuItem(),
            new MenuItem("Open") {
              disable = true
              accelerator =
                new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown)
            }
          )
        },
        new Menu("Edit") {
          items = List(
            new MenuItem("Select") {
              onAction = (_: ActionEvent) =>
                controller.switchMode(
                  GraphCanvasController.EditingMode.Selecting,
                  graphContainer.canvas.redraw
                )
              accelerator =
                new KeyCodeCombination(KeyCode.M, KeyCombination.AltDown)
            }
          )
        },
        new Menu("Draw") {
          items = List(
            new MenuItem("Source") {
              onAction = (_: ActionEvent) =>
                controller.switchMode(
                  GraphCanvasController.EditingMode.Source,
                  graphContainer.canvas.redraw
                )
              accelerator =
                new KeyCodeCombination(KeyCode.S, KeyCombination.AltDown)
            },
            new SeparatorMenuItem(),
            new MenuItem("Edges") {
              onAction = (_: ActionEvent) =>
                controller.switchMode(
                  GraphCanvasController.EditingMode.BeginEdge,
                  graphContainer.canvas.redraw
                )
              accelerator =
                new KeyCodeCombination(KeyCode.E, KeyCombination.AltDown)
            }
          )
        }
      )
    }

    center = graphContainer

    bottom = statusBar
  }
}
