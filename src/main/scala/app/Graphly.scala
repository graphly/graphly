package app

import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory
import javafx.stage.WindowEvent
import scalafx.application.JFXApp
import ui.AppMainSceneView
import scalafx.Includes._

object Graphly extends JFXApp {
  SvgImageLoaderFactory.install()
  stage = new JFXApp.PrimaryStage {
    title = "Graphly"
    scene = new AppMainSceneView(800, 600) {
      stylesheets += getClass.getResource("style.css").toExternalForm
      onCloseRequest = (e: WindowEvent) => {
        if (this.hasChanges) {
          if (!this.checkUserWantsExit) {
            e.consume()
          }
        }
      }
    }
  }
}
