package util

trait Default[A] {
  def default: A
}

package object default {
  def default[A](implicit default: Default[A]): A = default.default
}