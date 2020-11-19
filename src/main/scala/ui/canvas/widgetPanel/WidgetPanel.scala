package ui.canvas.widgetPanel

import scalafx.geometry.Insets
import scalafx.scene.layout.VBox
import ui.canvas.GraphCanvasController

class WidgetPanel extends VBox {
  def widget(widget: Widget): Unit = {
    children.add(widget)
  }
}

object WidgetPanel {
  object Element {
    def apply(controller: GraphCanvasController[_]): WidgetPanel = {
      val panel = new WidgetPanel
      panel.setPadding(Insets(10))
      panel.setSpacing(8)
      panel.minWidth = 200
      panel.maxWidth = 400
      panel.setStyle("-fx-background-color: #D4D4D4;")
      panel.widget(Widget("testing123"))
      panel
    }
  }
}
