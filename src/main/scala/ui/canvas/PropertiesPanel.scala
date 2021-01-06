package ui.canvas

import scalafx.geometry.HPos
import scalafx.scene.Node
import scalafx.scene.control.{ComboBox, Label, TextField, TextFormatter}
import scalafx.scene.layout.{GridPane, Pane, Priority}
import scalafx.util.converter.{DoubleStringConverter, IntStringConverter}

class PropertiesPanel extends GridPane {
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

  def textField(title: String, initial: String): Unit = {
    addTextField(title, initial, null)
  }

  def integerField(title: String, initial: Int): Unit = {
    val converter = new IntStringConverter
    addTextField(title, initial.toString, new TextFormatter(converter))
  }

  def doubleField(title: String, initial: Double): Unit = {
    val converter = new DoubleStringConverter
    addTextField(title, initial.toString, new TextFormatter(converter))
  }

  private def addTextField(title: String, initial: String, formatter: TextFormatter[_]): Unit = {
    val textField = new TextField {
      textFormatter = formatter
    }
    textField.text = initial
    this.addRowSpaced(rowCounter, wideLabel(title), textField)
    rowCounter += 1
  }

  def dropdown(
      title: String,
      options: List[String],
      placeholder: String
  ): Unit                                                 = {
    val box = new ComboBox[String](options)
    box.setValue(placeholder)
    this.addRowSpaced(rowCounter, wideLabel(title), box)
    rowCounter += 1
  }

  private def addRowSpaced(index: Int, left: Node, right: Node): Unit = {
    val spacer = new Pane
    spacer.prefWidth = 40
    GridPane.setHgrow(right, Priority.Always)
    GridPane.setHalignment(right, HPos.Right)
    this.addRow(index, left, spacer, right)
  }

  def clearAll(): Unit                                    = {
    children.removeAll()
    rowCounter = 0
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
          nodes.head.metadata.foreach((menu.textField _).tupled)
          menu.show()
        case _: GraphCanvasController.EditingMode.SelectEdge =>
          menu.show()
        case _ => menu.hide()
      }
      menu
    }
  }

  object Sim     {
    def apply(controller: GraphCanvasController[_]): PropertiesPanel = {
      val panel = new PropertiesPanel
      controller.model.configuration.foreach {
        case (title, configuration) =>
          panel.textField(title, configuration.toString)
      }
      panel
    }
  }
}
