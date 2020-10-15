package ui

import javafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.BorderPane
import ui.canvas.GraphCanvasContainer

class AppMainSceneView(width: Double, height: Double) extends Scene(width, height) {
  private val graphContainer = new GraphCanvasContainer
  private val graph = graphContainer.canvas

  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("Save"),
        new Menu("Open"),
        new Menu("Drawing Preferences") {
          items = List(
            new MenuItem("Nodes") {
              onAction = (_: ActionEvent) => graph.drawingModeNodes()
              accelerator = new KeyCodeCombination(KeyCode.N, KeyCombination.AltDown)
            },
            new MenuItem("Edges") {
              onAction = (_: ActionEvent) => graph.drawingModeEdges()
              accelerator = new KeyCodeCombination(KeyCode.E, KeyCombination.AltDown)
            }
          )
        }
      )
    }

    center = graphContainer
  }
}
