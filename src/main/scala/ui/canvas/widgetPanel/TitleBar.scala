package ui.canvas.widgetPanel

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Pane, Priority, StackPane}
import scalafx.scene.text.Text

class TitleBar(title: String) extends HBox {
  private val titleElem = new Text(title)
  val minimise: StackPane = icon("./assets/icons/triangle-32.png")
  minimise.setPadding(Insets(0, 8, 0, 0))
  val filler = new Pane()

  prefHeight = 30
  fillHeight = true
  alignment = Pos.CenterLeft
  styleClass = List("widget-titlebar")

  children.addAll(titleElem, filler, minimise)
  HBox.setHgrow(filler, Priority.Always)

  def icon(path: String): StackPane = {
    val iconImage = new ImageView(path)
    iconImage.preserveRatio = true
    iconImage.fitWidth = 11
    iconImage.opacity = 0.6
    iconImage.pickOnBounds = true

    val stackPane =  new StackPane()
    stackPane.children.add(iconImage)
    stackPane.setStyle("-fx-cursor: hand;")
    stackPane
  }
}
