package app

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.image.Image
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType

// An object is a *named* singleton
object Graphly extends JFXApp {
  /* `def` makes this a method (or a "dynamic value" same thing),
      braces are used if they span multiple lines, the last (or only) line is returned (like Rust, Kotlin) */
  def mkCanvas(): Canvas = new Canvas {
    /* This is a new object of an anonymous subclass of Canvas, with the code below run as the object body.
       This is the same as an anonymous class in Java, except the body in Scala is the same as the default
       constructor (as in Kotlin)
     */

    // All of these are not assignments, but setter calls to methods
    width = 600
    height = 450
    // The above are equivalent to
    this.width_=(600)
    this.height_=(450)

    // `val` introduces a binding or a field, the type can be inferred, or specified with: <name>: <type>
    private val image = new Image("https://cyan.com/wp-content/uploads/2019/08/test-image.jpg")
    private val image_ = new Image("https://homepages.cae.wisc.edu/~ece533/images/cat.png")

    // Custom method defined below to clean our the canvas
    makePrettyAndClean()

    /* This is not an assignment, but a call to the onMouseClicked setter method,
       which is passed the lambda (e: ...) => { ... } */
    onMouseClicked = (e: MouseEvent) => {
      // Pattern matching is: <value> match {  case <Pattern> => ... }
      e.button match {
        case MouseButton.Primary =>
          /* Call the getter `graphicsContext2D`, equivalent to graphicsContext2D()
             We could use graphicsContext2D inline, but I decided to assign it here to avoid property calls */
          val context: GraphicsContext = graphicsContext2D // Here's the <name>: <type> variant for fun
          context.drawImage(image, 0, 0)
          context.drawImage(image_, e.sceneX, e.sceneY)
          context.fill = Color.CornflowerBlue
          context.fillArc(50, 200, 150, 150, 0, 360, ArcType.Chord)
        case MouseButton.Secondary => makePrettyAndClean()
      }
    }

    /* This is a multiline function. The `=` is required because this is like Haskell */
    def makePrettyAndClean(): Unit = {
      val context = graphicsContext2D
      context.fill = Color.BlanchedAlmond
      context.fillRect(0, 0, width.get, height.get)
    }
  }

  stage = new JFXApp.PrimaryStage {
    title.value = "Graphly"
    width = 600
    height = 450
    scene = new Scene {
      content = mkCanvas()
    }
  }
}
