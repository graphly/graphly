package ui.canvas

import scalafx.geometry.Pos
import scalafx.scene.control.{ComboBox, Label, TextField}
import scalafx.scene.text.{Font, Text}
import scalafx.scene.layout.GridPane

class VerticalSettingsMenu(controller: GraphCanvasController[_]) extends GridPane {
  private var rowCounter = 1
  private val title = new Text("Placeholder")
  title.setFont(Font.font(20))
  addRow(0, title)
  managed <== visible
  visible = false

  controller.onSwitchMode +=
    {
      case _: GraphCanvasController.EditingMode.SelectNode => show()
      case _: GraphCanvasController.EditingMode.SelectEdge => show()
      case _ => hide()
    }

  def hide(): Unit = {
    visible = false
  }

  def show(): Unit = {
    visible = true
  }

  def setTitle(title: String): Unit = {
    this.title.text = title
  }

  def addTextField(title: String, placeholder: String): Unit = {
    val textField = new TextField()
    textField.setText(placeholder)
    this.addRow(rowCounter, new Label(title), textField)
    rowCounter += 1
  }

  def addDropdown(title: String, options: List[String], placeholder: String): Unit = {
    val dropdown = new ComboBox[String](options)
    dropdown.setValue(placeholder)
    this.addRow(rowCounter, new Label(title), dropdown)
    rowCounter += 1
  }

  this.setTitle("test 1")
  this.addTextField("TestField 1", "this is testing")
  this.addDropdown("Dropdown 1", List("testing 1", "testing 2", "testing 3"), "Select something")
  this.addTextField("TestField 2", "this is testing 2")
}

object VerticalSettingsMenu {
  def apply(controller: GraphCanvasController[_]): VerticalSettingsMenu = {
    val menu = new VerticalSettingsMenu(controller)
    menu.setHgap(10)
    menu.setVgap(10)
    menu.setPrefWidth(300)
    menu.setAlignment(Pos.TopCenter)
    menu.setStyle("-fx-background-color: #D4D4D4;")
    menu
  }
}
