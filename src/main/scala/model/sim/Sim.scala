package model.sim

import scala.collection.mutable

case class Sim(
    nodes: mutable.Set[Node],
    connections: mutable.Set[Connection],
    classes: mutable.Set[UserClass],
    measures: mutable.Set[Measure],
    traces: mutable.Buffer[Trace],
    configuration: Configuration,
    results: Option[xml.Node]
) {
  def +=(other: Sim): Unit = {
    nodes.addAll(other.nodes)
    connections.addAll(other.connections)
    classes.addAll(other.classes)
    measures.addAll(other.measures)
    traces.addAll(other.traces)
  }

  override def clone(): AnyRef = {
    Sim(
      this.nodes.clone(),
      this.connections.clone(),
      this.classes.clone(),
      this.measures.clone(),
      this.traces.clone(),
      this.configuration.clone(),
      this.results
    )
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case sim: Sim =>
        nodes.equals(sim.nodes) && connections.equals(sim.connections)
      case _ => false
    }
  }
}

case class Configuration(
    var seed: Long = 23000,
    var useRandomSeed: Boolean = true,
    var maximumDuration: Double = -1,
    var maxSimulatedTime: Double = -1,
    var maxSamples: Int = 1000000,
    var disableStatistic: Boolean = false,
    var maxEvents: Int = -1,
    var pollingInterval: Double = 1,
    var parametricAnalysisEnabled: Boolean = false,
    var loggingPath: String = System.getenv("HOME").concat("/JMT"),
    var loggingAutoAppend: String = "0",
    var loggingDelim: String = ",",
    var loggingDecimalSeparator: String = "."
) {

  override def clone(): Configuration = {
    Configuration(
      this.seed,
      this.useRandomSeed,
      this.maximumDuration,
      this.maxSimulatedTime,
      this.maxSamples,
      this.disableStatistic,
      this.maxEvents,
      this.pollingInterval,
      this.parametricAnalysisEnabled,
      this.loggingPath,
      this.loggingAutoAppend,
      this.loggingDelim,
      this.loggingDecimalSeparator
    )
  }
}

object Sim {

  def empty: Sim = {
    Sim(
      mutable.Set.empty,
      mutable.Set.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.ArrayBuffer.empty,
      Configuration(),
      None
    )
  }
}
