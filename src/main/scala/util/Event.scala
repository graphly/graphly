package util

import scala.collection.mutable

class Event[T] {
  private var _callbacks = new mutable.ArrayBuffer[T => Unit]

  final def +=(fn: T => Unit): Unit = {
    _callbacks += fn
  }

  final def -=(fn: T => Unit): Unit = {
    _callbacks -= fn
  }

  final def dispatch(arg: T): Unit = _callbacks.foreach(_(arg))
}
