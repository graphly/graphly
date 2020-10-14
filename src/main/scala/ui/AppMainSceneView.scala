package ui

import scalafx.scene.Scene
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane
import ui.canvas.GraphCanvas

class AppMainSceneView(width: Double, height: Double) extends Scene(width, height) {
  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("Save"),
        new Menu("Open"),
        new Menu("Drawing Preferences") {
          items = List(
            new MenuItem("Nodes"),
            new MenuItem("Edges"),
          )
        }
      )
    }
    center = new GraphCanvas
  }
}
