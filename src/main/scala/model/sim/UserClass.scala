package model.sim

import scalafx.scene.paint.Color

/*
 * JMT doesn't seem to care on import if we
 * don't have userClasses which aren't the refClass,
 * missing from the relevant nodes
 */
case class UserClass(
    name: String,
    priority: Int = 0,
    referenceSource: Option[Node],
    `type`: UserClass.Type,
    population: Int = 0,
    distribution: Option[Distribution],
    color: Color
)

object UserClass {
  sealed trait Type
  case object Open   extends Type
  case object Closed extends Type
}

sealed trait Distribution

case class Uniform(min: Double, max: Double)        extends Distribution
case class Exponential(lambda: Double)              extends Distribution
case class Pareto(alpha: Double, k: Double)         extends Distribution
case class Poisson(mean: Double)                    extends Distribution
case class UnimplementedDistribution(raw: xml.Node) extends Distribution
