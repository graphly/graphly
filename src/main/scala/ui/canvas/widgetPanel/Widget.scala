package ui.canvas.widgetPanel

import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafx.scene.shape.Rectangle

class Widget(title: String) extends BorderPane {
  private val titleBar = new TitleBar(title)
  private var minimised = false
//  private var clip = new Rectangle()
  style = "-fx-background-color: #fdfdfd;"
  top = titleBar
  managed <== visible

  titleBar.minimise.onMouseClicked = (_: MouseEvent) => minimise()

  def close(): Unit = {
    visible = false
  }

  def minimise(): Unit = {
    if (minimised) {
      maxHeight = 10
      titleBar.minimise.setScaleY(1)
    }
    else {
      maxHeight = 10
      prefHeight = 30
      titleBar.minimise.setScaleY(-1)
    }
    minimised = !minimised
  }
}
