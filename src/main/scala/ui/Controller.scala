package ui

import scalafx.Includes._
import scalafx.scene.Node
import scalafx.scene.input.{KeyEvent, MouseEvent}

trait Controlled[S] extends Node {
  val controller: Controller[S]
  val state: S

  onMouseClicked = controller.onMouseClick(_, state)

  onMousePressed = controller.onMousePress(_, state)

  onMouseReleased = controller.onMouseRelease(_, state)

  onMouseMoved = controller.onMouseMove(_, state)

  onMouseDragged = controller.onMouseDragged(_, state)

  onKeyTyped = controller.onKeyTyped(_, state)
}

trait Controller[S] {
  // scalafx MouseEvents for now, can use adapter, but key modifier property necessary
  def onMouseClick(event: MouseEvent, state: S): Unit   = ()
  def onMousePress(event: MouseEvent, state: S): Unit   = ()
  def onMouseRelease(event: MouseEvent, state: S): Unit = ()
  def onMouseMove(event: MouseEvent, state: S): Unit    = ()
  def onMouseDragged(event: MouseEvent, state: S): Unit = ()

  def onKeyTyped(event: KeyEvent, state: S): Unit = ()
}
