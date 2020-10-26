package io

import model.sim.Sim

trait SimRepresentation[A] {
  def represent(sim: Sim): A
}

object Implicit {
  implicit class RepresentableSim[W](sim: Sim)(implicit simRepresentation: SimRepresentation[W]) {
    def toRepresentation: W = simRepresentation.represent(sim)
  }
}