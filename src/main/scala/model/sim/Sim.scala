package model.sim

import scala.collection.mutable

case class Sim(
    nodes: mutable.Set[Node],
    connections: mutable.Set[Connection],
    classes: mutable.Set[UserClass],
    measures: mutable.Set[Measure],
    traces: mutable.Buffer[Trace],
    var seed: Long = 23000,
    var useRandomSeed: Boolean = true,
    var maximumDuration: Double = -1,
    var maxSimulatedTime: Double = -1,
    var maxSamples: Int = 1000000,
    var disableStatistic: Boolean = false,
    var maxEvents: Int = -1,
    var pollingInterval: Double = 1,
    var parametricAnalysisEnabled: Boolean = false,
    var loggingPath: String = "~/JMT",
    var loggingAutoAppend: String = "0",
    var loggingDelim: String = ",",
    var loggingdecimalSeparator: String = "."

) {
  def merge(other: Sim): Unit = {
    // Add everything from the other sim to this new one. Might involve re-working UUIDs and shit like that.
    nodes.addAll(other.nodes)
    connections.addAll(other.connections)
    classes.addAll(other.classes)
    measures.addAll(other.measures)
    traces.addAll(other.traces)
  }
}

object Sim {

  def empty: Sim = {
    Sim(
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.ArrayBuffer.empty
    )
  }
}
