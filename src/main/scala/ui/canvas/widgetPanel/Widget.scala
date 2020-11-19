package ui.canvas.widgetPanel

import scalafx.scene.layout.{BorderPane, Pane}
import scalafx.Includes._
import scalafx.scene.input.MouseEvent

class Widget(title: String) extends BorderPane {
  private val titleBarHeight = 30
  private val titleBar = new TitleBar(title, titleBarHeight)
  top = titleBar
  managed <== visible

  titleBar.close.onMouseClicked = (_: MouseEvent) => close()

  titleBar.minimise.onMouseClicked = (_: MouseEvent) => minimise()

  def close(): Unit = {
    visible = false
  }

  def minimise(): Unit = {
    minHeight = titleBarHeight
  }
}
