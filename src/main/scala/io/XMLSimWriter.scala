package io

import model.sim.Sim

object XMLSimWriter extends SimWriter[xml.Elem] {
  override def write(x: Sim): xml.Elem = {
    xml.Elem(null, "name", xml.Null, xml.TopScope, false, xml.Text("booyaa"))
  }

  implicit val xmlSimWriter: SimWriter[xml.Elem] = this
}