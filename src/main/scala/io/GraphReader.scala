package io

import model.graph.Graph

import scala.xml.Elem

trait GraphReader[A] {
  def read(x: A): Graph
}

package object implicits {
  implicit object XMLGraphReader extends GraphReader[xml.Elem] {
    override def read(x: Elem): Graph = ???
  }

  implicit class GraphReadableToGraph[X](readable: X)(implicit graphReader: GraphReader[X]) {
    def toGraph: Graph = graphReader.read(readable)
  }
}