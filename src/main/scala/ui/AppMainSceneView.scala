package ui

import javafx.event.ActionEvent
import model.sim.Sim
import scalafx.scene.Scene
import scalafx.scene.control.{Menu, MenuBar, MenuItem, SeparatorMenuItem}
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.BorderPane
import ui.canvas.{GraphCanvasContainer, GraphCanvasController}

class AppMainSceneView(width: Double, height: Double) extends Scene(width, height) {
  private val model: Sim = null
  private val graphContainer = new GraphCanvasContainer
  private val controller = new GraphCanvasController(graphContainer.canvas, model)

  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("Save"),
        new Menu("Open"),
        new Menu("Drawing Preferences") {
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
