package ui.canvas.widgetPanel

import scalafx.geometry.Insets
import scalafx.scene.layout.VBox

class WidgetPanel extends VBox {
  styleClass = List("widget-panel")

  def widget(widget: Widget): Unit = { children.add(widget) }

  def clear(): Unit = { children.clear() }
}

object WidgetPanel {
  object Element {
    def apply(): WidgetPanel = {
      val panel = new WidgetPanel
      panel.setPadding(Insets(10))
      panel.setSpacing(14)
      panel.minWidth = 200
      panel.maxWidth = 400
      panel
    }
  }
}
