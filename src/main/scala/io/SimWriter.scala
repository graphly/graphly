package io

import model.sim.Sim
import XMLSimRepresentation.xmlSimRepresentation

trait SimRepresentation[A] {
  def represent(sim: Sim): A
}

// class SimRepresentation a where
//   represent :: Sim -> A

// trait SimRepresentation {
//   fn represent(sim: Sim) -> Self
// }

object Implicits {
  // Eq a => a -> a -> Bool
  // SimWriter w => Sim -> w
  implicit class RepresentableSim[W](sim: Sim)(implicit simRepresentation: SimRepresentation[W]) {
    def toRepresentation: W = simRepresentation.represent(sim)
  }

  def iDontKnowWhatsHappening(): Unit = {
    val xml: scala.xml.Elem = Sim(null, null, null, null, null).toRepresentation
  }
}