package io

import model.sim.Sim

object XMLGraphWriter extends SimWriter[xml.Elem] {
  override def write(x: Sim): xml.Elem = {
    xml.Elem(null, "name", xml.Null, xml.TopScope, false, xml.Text("booyaa"))
  }

  implicit val xmlGraphWriter: SimWriter[xml.Elem] = XMLGraphWriter
}