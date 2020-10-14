package app

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.image.Image
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType
import ui.AppMainSceneView

// An object is a *named* singleton
object Graphly extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title = "Graphly"
    scene = new AppMainSceneView(800, 600)
  }
}
