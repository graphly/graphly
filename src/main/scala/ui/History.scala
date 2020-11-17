package ui

import model.sim.Sim

import scala.collection.mutable

class History {
  private val history = mutable.Queue[HistoryAction]()

  def undo(sim: model.sim.Sim): Boolean = {
    true
  }

  def redo(sim: model.sim.Sim): Boolean = {
    true
  }
}

trait HistoryAction {
  def commit(sim: model.sim.Sim)
  def revert(sim: model.sim.Sim)
}

class HANode(node: model.sim.Node, create: Boolean) extends HistoryAction {
  override def commit(sim: Sim): Unit = if (create) sim.nodes.add(node) else sim.nodes.remove(node)
  override def revert(sim: Sim): Unit = if (create) sim.nodes.remove(node) else sim.nodes.add(node)
}

class HANodeMove(node: model.sim.Node, start: model.Position, end: model.Position) extends HistoryAction {
  override def commit(sim: Sim): Unit = node.position = end
  override def revert(sim: Sim): Unit = node.position = start
}

class HAEdge(edge: model.sim.Connection, create: Boolean) extends HistoryAction {
  override def commit(sim: Sim): Unit = if (create) sim.connections.add(edge) else sim.connections.remove(edge)
  override def revert(sim: Sim): Unit = if (create) sim.connections.remove(edge) else sim.connections.add(edge)
}

class HATrace(trace: model.sim.Trace, create: Boolean) extends HistoryAction {
  override def commit(sim: Sim): Unit = if (create) sim.traces += trace else sim.traces.dropRightInPlace(1)
  override def revert(sim: Sim): Unit = if (create) sim.traces.dropRightInPlace(1) else sim.traces += trace
}
