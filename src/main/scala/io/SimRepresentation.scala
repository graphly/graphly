package io

import model.sim.Sim

trait SimRepresentation[A] {
  def represent(sim: Sim): A

  def toSim(xmlSim: A): Sim
}

object Implicit {
  implicit class RepresentableSim[W](sim: Sim)(implicit
      simRepresentation: SimRepresentation[W]
  ) {
    def toRepresentation: W = simRepresentation.represent(sim)
  }

  implicit class SimableRepresention[R](xmlSim: R)(implicit
      simRepresentation: SimRepresentation[R]
  ) {
    def toSim: Sim = simRepresentation.toSim(xmlSim)
  }
}
