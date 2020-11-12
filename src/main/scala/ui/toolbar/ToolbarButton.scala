package ui.toolbar

import scalafx.scene.control.{ToggleButton, Tooltip}
import scalafx.scene.image.{Image, ImageView}

class ToolbarButton(val tooltipText: String, val iconUrl: String)
    extends ToggleButton {
  val icon = new Image(iconUrl, 32, 32, false, false)

  this.tooltip = new Tooltip(tooltipText) { graphic = new ImageView(icon) }
  this.graphic = new ImageView(icon)
}
