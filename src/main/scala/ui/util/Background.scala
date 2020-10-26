package ui.util

import scalafx.scene.layout.{Background, BackgroundFill, CornerRadii}
import scalafx.geometry.Insets
import scalafx.scene.paint.Color

object Background {
  def apply(color: Color): Background =
    new Background(
      Array(new BackgroundFill(color, new CornerRadii(0), Insets(0, 0, 0, 0)))
    )

  object Implicit {
    implicit def backgroundFromColor(color: Color): Background =
      Background(color)
  }
}
