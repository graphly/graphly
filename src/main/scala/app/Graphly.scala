package app

import scalafx.application.JFXApp
import ui.AppMainSceneView

// An object is a *named* singleton
object Graphly extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Graphly"
    scene = new AppMainSceneView(800, 600)
  }
}
