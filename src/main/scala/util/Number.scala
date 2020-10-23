package util

object Number {
  object Implicit {
    implicit class DoubleExtensions(double: Double) {
      final def **(exponent: Double): Double = math.pow(double, exponent)
    }
    implicit class IntExtensions(integer: Int) {
      final def **(exponent: Int): Int = exponent match {
        case 1 => integer
        case Succ(exponent) => ((integer * integer) ** (exponent / 2)) * (if (exponent % 2 == 0) 1 else integer)
        case _ => 1
      }
    }
  }

  object Succ {
    def unapply(n: Int): Option[Int] = if (n > 0) Some(n) else None
  }
}
