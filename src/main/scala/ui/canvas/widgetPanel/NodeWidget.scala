package ui.canvas.widgetPanel

import model.sim.Sim
import ui.canvas.GraphCanvasController

class NodeWidget(title: String, model: Sim)
    extends PropertiesWidget(title, model) {}

object NodeWidget {
  def apply(
      title: String,
      model: Sim,
      graphController: GraphCanvasController[_]
  ): NodeWidget = {
    val widget = new NodeWidget(title, model)
    graphController.onSwitchMode += {
      case GraphCanvasController.EditingMode.SelectNode(nodes) =>
        if (nodes.size == 1) {
          widget.clear()
          widget.visible = true
          nodes.foreach(n => {
            widget.textField("Name", n.name, (_, y) => n.name = y)
            widget.checkbox("Rotated", n.rotated, (_, y) => n.rotated = y)
          })
        }
      case GraphCanvasController.EditingMode.DragNode(_, _, _) =>
      case _ => widget.visible = false
    }
    widget
  }
}
