package io

import model.sim.Sim

// instance SimRepresentation XML.Elem where
//   represent = xml.Elem Nothing "name" ...

// impl SimRepresentation for XML::Elem {
//   fn represent(boop: Sim) -> XML::Elem
// }

object XMLSimRepresentation extends SimRepresentation[xml.Elem] {
  override def represent(x: Sim): xml.Elem = {
    xml.Elem(null, "name", xml.Null, xml.TopScope, false, xml.Text("booyaa"))
  }

  implicit val xmlSimRepresentation: SimRepresentation[xml.Elem] = this
}