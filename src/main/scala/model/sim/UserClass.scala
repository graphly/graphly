package model.sim

case class UserClass(
    name: String,
    priority: Int,
    referenceSource: Node,
    `type`: UserClass.Type,
    population: Int,
    distribution: Distribution
)

object UserClass {
  sealed trait Type
  case object Open   extends Type
  case object Closed extends Type
}

sealed trait Distribution

case class Uniform(min: Double, max: Double) extends Distribution
case class Exponential(lambda: Double)       extends Distribution
case class Pareto(alpha: Double, k: Double)  extends Distribution
case class Poisson(mean: Double)             extends Distribution
