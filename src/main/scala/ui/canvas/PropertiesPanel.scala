package ui.canvas

import scalafx.geometry.HPos
import scalafx.scene.Node
import scalafx.scene.control.{ComboBox, Label, TextField}
import scalafx.scene.layout.{GridPane, Pane, Priority}

class PropertiesPanel extends GridPane {
  private var rowCounter = 0

  managed <== visible
  styleClass = List("prop-panel")

  def hide(): Unit = { visible = false }

  def show(): Unit = { visible = true }

  def label(str: String): Label = {
    val lbl = new Label(str)
    lbl.minWidth = 40
    lbl
  }

  def textField(title: String, placeholder: String): Unit = {
    val textField = new TextField()
    textField.text = placeholder
    this.addRowSpaced(rowCounter, label(title), textField)
    rowCounter += 1
  }

  def integerField(title: String, placeholder: Int): Unit = {
    ???
  }

  def floatField(title: String, placeholder: Float): Unit = {
    ???
  }

  def dropdown(
      title: String,
      options: List[String],
      placeholder: String
  ): Unit                                                 = {
    val box = new ComboBox[String](options)
    box.setValue(placeholder)
    this.addRowSpaced(rowCounter, label(title), box)
    rowCounter += 1
  }

  def addRowSpaced(index: Int, left: Node, right: Node): Unit = {
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
