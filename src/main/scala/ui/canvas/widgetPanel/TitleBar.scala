package ui.canvas.widgetPanel

import scalafx.geometry.{Insets, Pos}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.{HBox, Pane, Priority, StackPane}
import scalafx.scene.text.Text

class TitleBar(title: String, height: Int) extends HBox {
  private val titleElem = new Text(title)
  val close: StackPane = icon("./assets/icons/close-32.png")
  val minimise: StackPane = icon("./assets/icons/minimise-32.png")
  minimise.setPadding(Insets(0, 13, 0, 0))
  val filler = new Pane()

  prefHeight = height
  fillHeight = true
  alignment = Pos.CenterLeft
  padding = Insets(5, 10, 5, 10)
  style = "-fx-background-color: #D4D4D4;"

  children.addAll(titleElem, filler, minimise, close)
  HBox.setHgrow(filler, Priority.Always)

  def icon(path: String): StackPane = {
    val iconImage = new ImageView(path)
    iconImage.fitWidth = 12
    iconImage.fitHeight = 12
    iconImage.opacity = 0.6
    iconImage.pickOnBounds = true

    val stackPane =  new StackPane()
    stackPane.children.add(iconImage)
    stackPane.setStyle("-fx-cursor: hand;")
    stackPane
  }
}
