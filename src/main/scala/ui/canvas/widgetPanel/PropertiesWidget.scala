package ui.canvas.widgetPanel

import ui.canvas.PropertiesPanel

class PropertiesWidget(title: String) extends Widget(title: String)  {
  val propertiesPanel = new PropertiesPanel
  center = propertiesPanel
}
