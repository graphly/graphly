package model.sim

import scala.collection.mutable

case class Sim(
    nodes: mutable.Set[Node],
    connections: mutable.Set[Connection],
    classes: mutable.Set[UserClass],
    measures: mutable.Set[Measure],
    traces: mutable.Buffer[Trace],
    configuration: Sim.Configuration = mutable.HashMap.empty,
    seed: Long = 23000,
    useRandomSeed: Boolean = true,
    maximumDuration: Double = -1,
    maxSimulatedTime: Double = -1,
    maxSamples: Int = 1000000,
    disableStatistic: Boolean = false,
    maxEvents: Int = -1,
    pollingInterval: Double = 1,
    parametricAnalysisEnabled: Boolean = false
)

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
