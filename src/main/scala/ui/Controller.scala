package ui

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.input.{InputEvent, KeyEvent, MouseEvent, ScrollEvent}
import WithPosition.Implicit._

trait Controlled[S] extends Node {
  val controller: Controller[S]
  val state: S

  def transform(screen: ui.Position): model.Position

  import LogicalEvent.withTransform

  onMouseClicked =
    e => controller.onMouseClick(withTransform[MouseEvent](e, transform), state)

  onMousePressed =
    e => controller.onMousePress(withTransform[MouseEvent](e, transform), state)

  onMouseReleased = e =>
    controller.onMouseRelease(withTransform[MouseEvent](e, transform), state)

  onMouseMoved =
    e => controller.onMouseMove(withTransform[MouseEvent](e, transform), state)

  onMouseDragged = e =>
    controller.onMouseDragged(withTransform[MouseEvent](e, transform), state)

  onScroll =
    e => controller.onScroll(withTransform[ScrollEvent](e, transform), state)

  onKeyTyped = controller.onKeyTyped(_, state)
}

trait Controller[S] {
  // scalafx MouseEvents for now, can use adapter, but key modifier property necessary
  def onMouseClick(event: LogicalEvent[MouseEvent], state: S): Unit   = ()
  def onMousePress(event: LogicalEvent[MouseEvent], state: S): Unit   = ()
  def onMouseRelease(event: LogicalEvent[MouseEvent], state: S): Unit = ()
  def onMouseMove(event: LogicalEvent[MouseEvent], state: S): Unit    = ()
  def onMouseDragged(event: LogicalEvent[MouseEvent], state: S): Unit = ()

  def onScroll(event: LogicalEvent[ScrollEvent], state: S): Unit = ()

  def onKeyTyped(event: KeyEvent, state: S): Unit = ()
}

case class LogicalEvent[E](
    modelPosition: model.Position,
    screenPosition: ui.Position,
    event: E
)

object LogicalEvent {
  def zeroed[E](event: E): LogicalEvent[E] =
    LogicalEvent(model.Position.Zero, ui.Position.Zero, event)

  def withTransform[P: WithPosition](
      event: P,
      transform: ui.Position => model.Position
  ): LogicalEvent[P] =
    LogicalEvent(transform(event.position), event.position, event)
}
