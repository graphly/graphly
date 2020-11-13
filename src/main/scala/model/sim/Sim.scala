package model.sim

import scala.collection.mutable

case class Sim(
    nodes: mutable.Set[Node],
    connections: mutable.Set[Connection],
    classes: mutable.Set[UserClass],
    measures: mutable.Set[Measure],
    traces: mutable.Buffer[Trace],
    configuration: Sim.Configuration = mutable.HashMap.empty
) {
  def merge(other: Sim): Unit = {
    // Add everything from the other sim to this new one. Might involve re-working UUIDs and shit like that.
    nodes.addAll(other.nodes)
    connections.addAll(other.connections)
    classes.addAll(other.classes)
    measures.addAll(other.measures)
    traces.addAll(other.traces)
    configuration.addAll(other.configuration)
  }

  override def clone(): AnyRef = {
    Sim(
      this.nodes.clone(),
      this.connections.clone(),
      this.classes.clone(),
      this.measures.clone(),
      this.traces.clone(),
      this.configuration.clone(),
    )
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case sim: Sim =>
        nodes.equals(sim.nodes) && connections.equals(sim.connections)
      case _ =>
        false
    }
  }
}

object Sim {
  type Configuration = mutable.Map[String, Any]

  def empty: Sim = {
    Sim(
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.ArrayBuffer.empty,
      mutable.HashMap.empty
    )
  }
}
