package ui.canvas.widgetPanel

import scalafx.scene.layout.BorderPane
import scalafx.Includes._
import scalafx.scene.input.MouseEvent

class Widget(title: String) extends BorderPane {
  private val titleBar = new TitleBar(title)
  private var minimised = false
  top = titleBar
  styleClass = List("widget")
  managed <== visible

  titleBar.onMouseClicked = (_: MouseEvent) => minimise()

  def close(): Unit = {
    visible = false
  }

  def minimise(): Unit = {
    if (minimised) {
      center.value.setVisible(true)
      titleBar.minimise.setScaleY(1)
    }
    else {
      center.value.setVisible(false)
      titleBar.minimise.setScaleY(-1)
    }
    minimised = !minimised
  }
}
