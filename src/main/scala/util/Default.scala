package util

trait Default[A] {
  def default: A
}

object Default {
  def default[A](implicit default: Default[A]): A = default.default

  implicit def apply[A](value: => A): Default[A] = new Default[A] { override def default: A = value }
}