package model.sim

import scala.collection.mutable

case class Sim(
    nodes: mutable.Set[Node],
    connections: mutable.Set[Connection],
    classes: mutable.Set[UserClass],
    measures: mutable.Set[Measure],
    traces: mutable.Buffer[Trace],
    configuration: Sim.Configuration = mutable.HashMap.empty
)

object Sim {
  type Configuration = mutable.HashMap[String, Any]

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
