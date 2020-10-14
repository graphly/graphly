package io

import model.sim.Sim

trait SimWriter[A] {
  def write(sim: Sim): A
}

object Implicits {
  implicit class WritableSim[W](sim: Sim)(implicit simWriter: SimWriter[W]) {
    def toWritable: W = simWriter.write(sim)
  }
}