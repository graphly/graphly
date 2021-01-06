package ui.canvas

import scalafx.geometry.Pos
import scalafx.scene.control.{ComboBox, Label, TextField}
import scalafx.scene.layout.GridPane
import scalafx.scene.text.{Font, Text}

class PropertiesPanel extends GridPane {
  private var rowCounter = 1
  private val heading    = new Text("Placeholder")
  heading.setFont(Font.font(20))
  addRow(0, heading)
  managed <== visible

  def hide(): Unit = { visible = false }

  def show(): Unit = { visible = true }

  def title = this.heading.text

  def title_=(heading: String): Unit = { this.heading.text = heading }

  def textField(title: String, placeholder: String): Unit = {
    val textField = new TextField()
    textField.text = placeholder
    this.addRow(rowCounter, new Label(title), textField)
    rowCounter += 1
  }

  def dropdown(
      title: String,
      options: List[String],
      placeholder: String
  ): Unit                                                 = {
    val box = new ComboBox[String](options)
    box.setValue(placeholder)
    this.addRow(rowCounter, new Label(title), box)
    rowCounter += 1
  }

  def clearAll(): Unit                                    = {
    children.retainAll(heading)
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
          menu.title = nodes.head.name
          /* TODO The sim configuration object has changed, this is to better reflect JMT
          This is the code for the previous system, it's purely for reference and will not work

          nodes.head.metadata.foreach((menu.textField _).tupled)
           */
          menu.show()
        case _: GraphCanvasController.EditingMode.SelectEdge =>
          menu.title = "Edge"
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
      panel.title = "Sim"
      /* TODO The sim configuration object has changed, this is to better reflect JMT
      This is the code for the previous system, it's purely for reference and will not work

       controller.model.configuration.foreach {
         case (title, configuration) =>
           panel.textField(title, configuration.toString)
       }
       */
      panel
    }
  }
}
