package ui.canvas

import scalafx.scene.layout.GridPane
import scalafx.scene.text.Text

class FloatingInfoBox() extends GridPane {
  this.addRow(0, new Text("testing123"))
  layoutX = 100
  layoutY = 100
}

object FloatingInfoBox {
  def apply(): FloatingInfoBox = {
    val box = new FloatingInfoBox()
    box.setStyle("-fx-background-color: red;")
    box
  }
}
