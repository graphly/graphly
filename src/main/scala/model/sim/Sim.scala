package model.sim

import java.io.{ByteArrayInputStream, FileInputStream, InputStream}

import scala.io
import scala.collection.mutable

case class Sim(
    nodes: mutable.Set[Node],
    connections: mutable.Set[Connection],
    classes: mutable.Set[UserClass],
    measures: mutable.Set[Measure],
    traces: mutable.Set[Trace],
    configuration: Sim.Configuration = mutable.HashMap.empty
)

object Sim {
  type Configuration = mutable.HashMap[String, Any]

  def empty: Sim = {
    // TODO REMOVE, CURRENT TEST

    val stream = new FileInputStream(
      "/home/matthewross/Pictures/Screenshot from 2020-10-15 17-26-20.png"
    )

    Sim(
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet.empty,
      mutable.HashSet(Trace(Trace.Image(stream))),
      mutable.HashMap.empty
    )
  }
}
