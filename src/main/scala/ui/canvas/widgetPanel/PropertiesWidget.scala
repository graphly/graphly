package ui.canvas.widgetPanel

import scalafx.scene.text.Text
import ui.canvas.PropertiesPanel

class PropertiesWidget(title: String) extends Widget(title: String)  {
  center = new PropertiesPanel.Element()
}
