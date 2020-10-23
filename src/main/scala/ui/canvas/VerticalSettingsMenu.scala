package ui.canvas

import scalafx.geometry.Pos
import scalafx.scene.control.{ComboBox, Label, TextField}
import scalafx.scene.text.{Font, Text}
import scalafx.scene.layout.GridPane

class VerticalSettingsMenu extends GridPane {
  private var rowCounter = 1
  private val title = new Text("Placeholder")
  prefWidth = 300
  style = "-fx-background-color: #D4D4D4;"
  alignment = Pos.TopCenter
  hgap = 10
  vgap = 10
  title.setFont(Font.font(20))
  addRow(0, title)
  managed <== visible
  visible = false

  def retract(): Unit = {
    visible = false
  }

  def extend(): Unit = {
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
  this.setTitle("test 2")
  this.addTextField("TestField 1", "this is testing")
  this.addDropdown("Dropdown 1", List("testing 1", "testing 2", "testing 3"), "Select something")
  this.addTextField("TestField 2", "this is testing 2")
}
