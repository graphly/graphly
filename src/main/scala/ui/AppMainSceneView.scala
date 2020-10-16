package ui

import javafx.event.ActionEvent
import model.sim.Sim
import scalafx.scene.Scene
import scalafx.scene.control.{Menu, MenuBar, MenuItem, SeparatorMenuItem}
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.BorderPane
import ui.canvas.{GraphCanvasContainer, GraphCanvasController}

class AppMainSceneView(width: Double, height: Double) extends Scene(width, height) {
  private val model: Sim = Sim.empty
  private val controller = new GraphCanvasController(model)
  private val graphContainer = new GraphCanvasContainer(controller)

  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("File") {
          items = List(
            new MenuItem("Save") {
              onAction = (_: ActionEvent) => {
                controller.save()
              }
              accelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.ControlDown)
            },
            new MenuItem("Save As") {
              disable = true
              accelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.ControlDown, KeyCombination.AltDown)
            },
            new SeparatorMenuItem(),
            new MenuItem("Open") {
              disable = true
              accelerator = new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown)
            },
          )
        },
        new Menu("Draw") {
          items = List(
            new MenuItem("Source") {
              onAction = (_: ActionEvent) => controller.setDrawMode(GraphCanvasController.EditingMode.Source)
              accelerator = new KeyCodeCombination(KeyCode.S, KeyCombination.AltDown)
            },
            new SeparatorMenuItem(),
            new MenuItem("Edges") {
              onAction = (_: ActionEvent) => controller.setDrawMode(GraphCanvasController.EditingMode.Edge)
              accelerator = new KeyCodeCombination(KeyCode.E, KeyCombination.AltDown)
            }
          )
        }
      )
    }

    center = graphContainer
  }
}
