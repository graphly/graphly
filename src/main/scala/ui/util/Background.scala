package ui.util

import scalafx.scene.layout.{BackgroundFill, CornerRadii}
import scalafx.geometry.Insets
import scalafx.scene.layout
import scalafx.scene.paint.Color

object Background {
  object Implicit {
    implicit def backgroundFromColor(color: Color): layout.Background = new layout.Background(Array(new BackgroundFill(color, new CornerRadii(0), Insets(0, 0, 0, 0))))
  }
}
