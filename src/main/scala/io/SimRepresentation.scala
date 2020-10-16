package io

import model.sim.Sim
import XMLSimRepresentation.xmlSimRepresentation

trait SimRepresentation[A] {
  def represent(sim: Sim): A
}
object Implicits {
  implicit class RepresentableSim[W](sim: Sim)(implicit simRepresentation: SimRepresentation[W]) {
    def toRepresentation: W = simRepresentation.represent(sim)
  }
}