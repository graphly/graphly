package ui.util

import scalafx.geometry.Insets
import scalafx.scene.layout.{Background => SfxBackground, BackgroundFill, CornerRadii}
import scalafx.scene.paint.Color

object Background {
  def apply(color: Color): SfxBackground =
    new SfxBackground(
      Array(new BackgroundFill(color, new CornerRadii(0), Insets(0, 0, 0, 0)))
    )

  object Implicit {
    implicit def backgroundFromColor(color: Color): SfxBackground =
      Background(color)
  }
}
