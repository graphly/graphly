package ui.util

import scalafx.beans.binding.{Bindings, ObjectBinding}
import scalafx.beans.value.ObservableValue
import scalafx.delegate.SFXDelegate

object Property {
  object Implicit {
    implicit class ObservableFunctor[T](val objectProperty: ObservableValue[T, _]) {
      def map[U](f: T => U): ObjectBinding[U] = Bindings.createObjectBinding(() => f(objectProperty.value), objectProperty)
    }

    implicit def observableConverter[T, U](objectProperty: ObservableValue[T, _])(implicit conversion: T => U): ObjectBinding[U] = {
      ObservableFunctor(objectProperty) map (conversion(_))
    }

    implicit class ObservableJFXConverter[+T](objectProperty: ObservableValue[T, _]) {
      def convertJFX[U <: Object](implicit conversion: T => SFXDelegate[U]): ObjectBinding[U] = ObservableFunctor(objectProperty) map (conversion(_).delegate)
    }

    implicit class ObservableToJFX[J <: Object](objectProperty: ObservableValue[_ <: SFXDelegate[_ <: J], _]) {
      def toJFX: ObjectBinding[J] = ObservableFunctor(objectProperty) map (_.delegate)
    }

    implicit def observableToJFX[J <: Object](objectProperty: ObservableValue[_ <: SFXDelegate[_ <: J], _]): ObjectBinding[J] = {
      objectProperty.toJFX
    }
  }
}
