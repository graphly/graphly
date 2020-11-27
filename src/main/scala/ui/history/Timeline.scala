package ui.history

import model.sim.Sim

import scala.collection.mutable

class Timeline       {
  private val history = mutable.Stack.empty[Action]
  private val future  = mutable.Stack.empty[Action]

  private def transfer[A](sim: model.sim.Sim, from: mutable.Stack[A], to: mutable.Stack[A])(apply: (A, model.sim.Sim) => ()): Boolean =
    if (from.nonEmpty) {
      val action = from.pop()
      apply(action, sim)
      to.push(action)
      true
    } else false

  def undo(sim: model.sim.Sim): Boolean = transfer(sim, history, future)(_.revert(_))

  def redo(sim: model.sim.Sim): Boolean = transfer(sim, future, history)(_.commit(_))

  def apply(action: Action, sim: model.sim.Sim): Unit = {
    action.commit(sim)
    +=(action)
  }

  def +=(action: Action): Unit = {
    history.push(action)
    future.removeAll()
  }
}

sealed trait Action {
  def commit(sim: model.sim.Sim): Unit
  def revert(sim: model.sim.Sim): Unit

  def +(actions: Action*): Action = CombinedAction(this +: actions : _*)
}

case class CombinedAction(actions: Action*) extends Action {
  override def commit(sim: Sim): Unit = actions.foreach(_.commit(sim))
  override def revert(sim: Sim): Unit = actions.foreach(_.revert(sim))
}

case class Move[A <: model.Positioned](
    elements: collection.Set[A],
    delta: model.Position
) extends Action {
  override def commit(sim: Sim): Unit = elements.foreach(_.position += delta)
  override def revert(sim: Sim): Unit = elements.foreach(_.position -= delta)
}

class Add[A](elements: collection.Set[A], projection: Sim => mutable.Set[A]) extends Action {
  override def commit(sim: Sim): Unit = projection(sim) ++= elements

  override def revert(sim: Sim): Unit = projection(sim) --= elements
}

object Add {
  class AddBuffer[A](elements: collection.Set[A], projection: Sim => mutable.Buffer[A]) extends Action {
    override def commit(sim: Sim): Unit = projection(sim) ++= elements

    override def revert(sim: Sim): Unit = projection(sim) --= elements
  }

  def node(elements: collection.Set[model.sim.Node]): Add[model.sim.Node] = new Add(elements, _.nodes)

  def edge(elements: collection.Set[model.sim.Connection]): Add[model.sim.Connection] = new Add(elements, _.connections)

  def trace(elements: collection.Set[model.sim.Trace]): AddBuffer[model.sim.Trace] = new AddBuffer(elements, _.traces)
}

case class Delete[A](elements: collection.Set[A], projection: Sim => mutable.Set[A]) extends Action {
  override def commit(sim: Sim): Unit = projection(sim) --= elements
  override def revert(sim: Sim): Unit = projection(sim) ++= elements
}

object Delete {
  class DeleteBuffer[A](elements: collection.Set[A], projection: Sim => mutable.Buffer[A]) extends Action {
    override def commit(sim: Sim): Unit = projection(sim) --= elements

    override def revert(sim: Sim): Unit = projection(sim) ++= elements
  }

  def node(elements: collection.Set[model.sim.Node]): Delete[model.sim.Node] = new Delete(elements, _.nodes)

  def edge(elements: collection.Set[model.sim.Connection]): Delete[model.sim.Connection] = new Delete(elements, _.connections)

  def trace(elements: collection.Set[model.sim.Trace]): DeleteBuffer[model.sim.Trace] = new DeleteBuffer(elements, _.traces)
}


case class ResizeTrace(elements: collection.Set[model.sim.Trace], xFactor: Double, yFactor: Double) extends Action {
  override def commit(sim: Sim): Unit = elements.foreach { element =>
    element.width *= xFactor
    element.height *= yFactor
  }

  override def revert(sim: Sim): Unit = elements.foreach { element =>
    element.width /= xFactor
    element.height /= yFactor
  }
}
