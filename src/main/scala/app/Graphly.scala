package app

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import scalafx.application.JFXApp
import ui.AppMainSceneView
import scalafx.Includes._

object Graphly extends JFXApp {
  SvgImageLoaderFactory.install()
  stage = new JFXApp.PrimaryStage {
    title = "Graphly"
    scene = new AppMainSceneView(800, 600) {
      stylesheets += getClass.getResource("style.css").toExternalForm
    }
  }
}
