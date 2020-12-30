package ui

import model.Position
import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.input.{InputEvent, KeyEvent, MouseEvent, ScrollEvent}

trait Controlled[S] extends Node {
  val controller: Controller[S]
  val state: S

  // TODO: Can we consolidate these to something better?
  def augmentMouseEvent(e: MouseEvent): GPosEvent[MouseEvent] =
    new GPosEvent[MouseEvent](Position.Zero, Position.Zero, e)
  def augmentScrollEvent(e: ScrollEvent): GPosEvent[ScrollEvent] =
    new GPosEvent[ScrollEvent](Position.Zero, Position.Zero, e)

  onMouseClicked =
    e => controller.onMouseClick(augmentMouseEvent(e), state)

  onMousePressed =
    e => controller.onMousePress(augmentMouseEvent(e), state)

  onMouseReleased =
    e => controller.onMouseRelease(augmentMouseEvent(e), state)

  onMouseMoved =
    e => controller.onMouseMove(augmentMouseEvent(e), state)

  onMouseDragged =
    e => controller.onMouseDragged(augmentMouseEvent(e), state)

  onScroll =
    e => controller.onScroll(augmentScrollEvent(e), state)

  onKeyTyped = controller.onKeyTyped(_, state)
}

trait Controller[S] {
  // scalafx MouseEvents for now, can use adapter, but key modifier property necessary
  def onMouseClick(event: GPosEvent[MouseEvent], state: S): Unit   = ()
  def onMousePress(event: GPosEvent[MouseEvent], state: S): Unit   = ()
  def onMouseRelease(event: GPosEvent[MouseEvent], state: S): Unit = ()
  def onMouseMove(event: GPosEvent[MouseEvent], state: S): Unit    = ()
  def onMouseDragged(event: GPosEvent[MouseEvent], state: S): Unit = ()

  def onScroll(event: GPosEvent[ScrollEvent], state: S): Unit = ()

  def onKeyTyped(event: KeyEvent, state: S): Unit = ()
}

class GPosEvent[E](
    val position: Position,
    val screenPosition: Position,
    val evt: E
)
