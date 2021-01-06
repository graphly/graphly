package ui.canvas.widgetPanel

class PropertiesWidget(title: String) extends Widget(title: String) {
  val propertiesPanel = new PropertiesPanel
  center = propertiesPanel

  def textField(title: String, initial: String): Unit   =
    propertiesPanel.textField(title, initial)
  def integerField(title: String, initial: Int): Unit   =
    propertiesPanel.integerField(title, initial)
  def doubleField(title: String, initial: Double): Unit =
    propertiesPanel.doubleField(title, initial)
  def dropdown(
      title: String,
      options: List[String],
      placeholder: String
  ): Unit                                               = propertiesPanel.dropdown(title, options, placeholder)
}
