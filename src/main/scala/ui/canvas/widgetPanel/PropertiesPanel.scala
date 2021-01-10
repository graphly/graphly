package ui.canvas.widgetPanel

import javafx.event.ActionEvent
import javafx.scene.input.{KeyCode, KeyEvent}
import model.sim.Sim
import scalafx.geometry.{HPos, VPos}
import scalafx.scene.Node
import scalafx.scene.control.{
  CheckBox,
  ComboBox,
  Label,
  TextField,
  TextFormatter
}
import scalafx.scene.layout.{GridPane, Pane, Priority}
import scalafx.util.converter.{DoubleStringConverter, IntStringConverter}

class PropertiesPanel(model: Sim) extends GridPane {
  private var rowCounter = 0

  managed <== visible
  styleClass = List("prop-panel")

  def hide(): Unit = { visible = false }

  def show(): Unit = { visible = true }

  def wideLabel(str: String): Label = {
    val lbl = new Label(str)
    lbl.minWidth = 40
    lbl
  }

  def textField(
      title: String,
      initial: String,
      projection: (Sim, String) => Unit
  ): Unit                           = { addField(title, x => y => projection(y, x), initial, null) }

  def integerField(
      title: String,
      initial: Int,
      projection: (Sim, Int) => Unit
  ): Unit                                                             = {
    val converter = new IntStringConverter
    addField(
      title,
      x => y => projection(y, converter.fromString(x)),
      initial.toString,
      new TextFormatter(converter)
    )
  }

  def doubleField(
      title: String,
      initial: Double,
      projection: (Sim, Double) => Unit
  ): Unit                                                             = {
    val converter = new DoubleStringConverter
    addField(
      title,
      x => y => projection(y, converter.fromString(x)),
      initial.toString,
      new TextFormatter(converter)
    )
  }

  private def addField(
      title: String,
      projection: String => Sim => Unit,
      initial: String,
      formatter: TextFormatter[_]
  ): Unit                                                             = {
    val textField = new TextField {
      textFormatter = formatter
      onKeyPressed = (key: KeyEvent) => {
        if (key.getCode.equals(KeyCode.ENTER)) {
          projection(this.text.value)(model)
        }
      }
    }
    textField.focusedProperty().addListener((_, _, newVal) => {
      if (!newVal) {
        println(title, textField.text.value)
        projection(textField.text.value)(model)
      }
    })
    textField.text = initial
    this.addRowSpaced(rowCounter, wideLabel(title), textField)
    rowCounter += 1
  }

  def dropdown(
      title: String,
      options: List[String],
      placeholder: String,
      projection: (Sim, String) => Unit
  ): Unit                                                             = {
    val box = new ComboBox[String](options) {
      onAction = (_: ActionEvent) => {
        println(title, this.value.value)
        projection(model, this.value.value)
      }
    }
    box.setValue(placeholder)
    this.addRowSpaced(rowCounter, wideLabel(title), box)
    rowCounter += 1
  }

  private def addRowSpaced(index: Int, left: Node, right: Node): Unit = {
    val spacer = new Pane
    spacer.prefWidth = 10
    spacer.minWidth = 5
    GridPane.setHgrow(right, Priority.Always)
    GridPane.setHalignment(right, HPos.Right)
    GridPane.setValignment(left, VPos.Center)
//    left.setStyle("-fx-background-color: lightblue;")
//    spacer.setStyle("-fx-background-color: lightgreen;")
//    right.setStyle("-fx-background-color: pink;")
    this.addRow(index, left, spacer, right)
  }

  def checkbox(
      title: String,
      initial: Boolean,
      projection: (Sim, Boolean) => Unit
  ): Unit                                                             = {
    val checkbox = new CheckBox() {
      onAction = (_: ActionEvent) => {
        println(title, this.isSelected)
        projection(model, this.isSelected)
      }
    }
    checkbox.selected = initial
    this.addRowSpaced(rowCounter, wideLabel(title), checkbox)
    rowCounter += 1
  }

  def clearAll(): Unit                                                = {
    children.clear()
    rowCounter = 0
  }
}
