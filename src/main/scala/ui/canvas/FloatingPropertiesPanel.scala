package ui.canvas

import scalafx.geometry.{HPos, Insets}
import scalafx.scene.layout.GridPane
import scalafx.scene.text.Text

class FloatingPropertiesPanel extends GridPane {
  private var rowCounter = 0
  private var x = 0
  private var y = 0
  managed <== visible

  def x_=(x: Double): Unit = { layoutX = x }

  def y_=(y: Double): Unit = { layoutY = y }

  def hide(): Unit = { visible = false }

  def show(): Unit = { visible = true }

  def row(name: String, value: String): Unit = {
    val shortenedName = if (name.length > 10) name.substring(0, 10) + "..." else name
    val nameElem = new Text(shortenedName + ":")
    val valueElem = if (value.strip().isEmpty) new Text("null") else new Text(value)
    this.addRow(rowCounter, nameElem, valueElem)
    GridPane.setHalignment(valueElem, HPos.Right)
    rowCounter += 1
  }

  def text(text: String): Unit = {
    this.addRow(0, new Text(text))
    rowCounter += 1
  }

  def clearAll(): Unit = {
    children.clear()
    rowCounter = 0
  }
}

object FloatingPropertiesPanel {
  object Element {
    def apply(controller: GraphCanvasController[_]): FloatingPropertiesPanel = {
      val box = new FloatingPropertiesPanel
      box.hide()
      controller.onSwitchMode += {
        case GraphCanvasController.EditingMode.FloatingMenu(nodes) =>
          box.show()
          box.clearAll()
          val node = nodes.head
          if (node.metadata.isEmpty) {
            box.text("Empty")
          }
          else {
            node.metadata.foreach((box.row _).tupled)
          }
          box.x = node.position.x + 40
          box.y = node.position.y - 25
        case _ =>
          box.hide()
      }
      box.setStyle("-fx-background-color: #EEEEEE;")
      box.setHgap(10)
      box.setVgap(10)
      box.setPadding(Insets(10, 10, 10, 10))
      box
    }
  }

  object Sim     {
    def apply(controller: GraphCanvasController[_]): FloatingPropertiesPanel = {
      val box = new FloatingPropertiesPanel
      controller.model.configuration.foreach {
        case (title, configuration) =>
          box.row(title, configuration.toString)
      }
      box
    }
  }
}
