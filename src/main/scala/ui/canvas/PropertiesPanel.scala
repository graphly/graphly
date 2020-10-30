package ui.canvas

import scalafx.geometry.Pos
import scalafx.scene.control.{ComboBox, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.scene.text.{Font, Text}

class PropertiesPanel extends GridPane {
  private var rowCounter = 1
  private val title      = new Text("Placeholder")
  title.setFont(Font.font(20))
  addRow(0, title)
  managed <== visible

  def hide(): Unit = { visible = false }

  def show(): Unit = { visible = true }

  def setTitle(title: String): Unit = { this.title.text = title }

  def addTextField(title: String, placeholder: String): Unit = {
    val textField = new TextField()
    textField.setText(placeholder)
    this.addRow(rowCounter, new Label(title), textField)
    rowCounter += 1
  }

  def addDropdown(
      title: String,
      options: List[String],
      placeholder: String
  ): Unit                                                    = {
    val dropdown = new ComboBox[String](options)
    dropdown.setValue(placeholder)
    this.addRow(rowCounter, new Label(title), dropdown)
    rowCounter += 1
  }

  def clearAll(): Unit                                       = {
    children.retainAll(title)
    rowCounter = 1
  }
}

object PropertiesPanel {
  object Element {
    def apply(controller: GraphCanvasController[_]): PropertiesPanel = {
      val menu = new PropertiesPanel
      menu.visible = false
      controller.onSwitchMode += {
        case GraphCanvasController.EditingMode.SelectNode(nodes) =>
          menu.clearAll()
          menu.setTitle(nodes.head.name)
          nodes.head.metadata.foreach((menu.addTextField _).tupled)
          menu.show()
        case _: GraphCanvasController.EditingMode.SelectEdge =>
          menu.setTitle("Edge")
          menu.show()
        case _ => menu.hide()
      }
      menu.setHgap(10)
      menu.setVgap(10)
      menu.setPrefWidth(300)
      menu.setAlignment(Pos.TopCenter)
      menu.setStyle("-fx-background-color: #D4D4D4;")
      menu
    }
  }

  object Sim     {
    def apply(controller: GraphCanvasController[_]): PropertiesPanel = {
      val panel = new PropertiesPanel
      panel.setTitle("Sim")
      controller.model.configuration.foreach {
        case (title, configuration) =>
          panel.addTextField(title, configuration.toString)
      }
      panel
    }
  }
}
