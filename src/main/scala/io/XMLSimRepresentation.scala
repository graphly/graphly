package io

import model.sim.Sim


object XMLSimRepresentation extends SimRepresentation[xml.Elem] {
  override def represent(x: Sim): xml.Elem = {
    xml.Elem(null, "name", xml.Null, xml.TopScope, false, xml.Text("booyaa"))
  }

  implicit val xmlSimRepresentation: SimRepresentation[xml.Elem] = this
}