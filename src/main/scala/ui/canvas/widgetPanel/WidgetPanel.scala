package ui.canvas.widgetPanel

import scalafx.geometry.Insets
import scalafx.scene.layout.VBox
import ui.canvas.GraphCanvasController

class WidgetPanel extends VBox {
  styleClass = List("widget-panel")

  def widget(widget: Widget): Unit = {
    children.add(widget)
  }
}

object WidgetPanel {
  object Element {
    def apply(controller: GraphCanvasController[_]): WidgetPanel = {
      val panel = new WidgetPanel
      panel.setPadding(Insets(10))
      panel.setSpacing(14)
      panel.minWidth = 200
      panel.maxWidth = 400
      panel
    }
  }
}
