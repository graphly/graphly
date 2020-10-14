package ui

import javafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Label, Menu, MenuBar, MenuItem, TextArea}
import scalafx.scene.layout.{BorderPane, HBox}
import ui.canvas.{GraphCanvas, GraphCanvasContainer}

class AppMainSceneView(width: Double, height: Double) extends Scene(width, height) {
  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("Save"),
        new Menu("Open"),
        new Menu("Drawing Preferences") {
          items = List(
            new MenuItem("Nodes") {
              onAction = (ev: ActionEvent) => {println(text.value + " clicked")}
            },
            new MenuItem("Edges") {
              onAction = (ev: ActionEvent) => {println(text.value + " clicked")}
            }
          )
        }
      )
    }

    center = new GraphCanvasContainer
  }
}
