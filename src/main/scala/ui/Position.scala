package ui

import scalafx.scene.input.MouseEvent
import model.sim
import _root_.util.Number.Implicit.DoubleExtensions

case class Position(x: Double, y: Double) {
  def model: sim.Position = sim.Position(x, y)

  def +(delta: Position): Position = Position(x + delta.x, y + delta.y)
  def -(delta: Position): Position = Position(x - delta.x, y - delta.y)
  def *(factor: Double): Position = Position(x * factor, y * factor)
  def /(factor: Double): Position = Position(x / factor, y / factor)

  def `.`(other: Position): Double = x * other.x + y * other.y
  def magnitude: Double = math.sqrt(x ** 2 + y ** 2)

  def <(other: Position): Boolean = x < other.x && y < other.y

  def inRectangle(start: Position, finish: Position): Boolean = {
    val xRect = x - start.x
    val yRect = y - start.y
    val xFinish = finish.x - start.x
    val yFinish = finish.y - start.y
    xRect.sign == xFinish.sign && yRect.sign == yFinish.sign &&
    xRect.abs < xFinish.abs && yRect.abs < yFinish.abs
  }

  def min(other: Position): Position = Position(x min other.x, y min other.y)
  def max(other: Position): Position = Position(x max other.x, y max other.y)
}

object Position {
  object Implicit {
    implicit class MouseEventPosition(mouseEvent: MouseEvent) {
      def position: Position = {
        Position(mouseEvent.x, mouseEvent.y)
      }
    }
  }

  def Zero: Position = Position(0, 0)
}

trait Positioned {
  var position: Position

  def x: Double = position.x
  def x_=(updated: Double): Unit = position = position.copy(x = updated)
  def y: Double = position.y
  def y_=(updated: Double): Unit = position = position.copy(y = updated)
}
