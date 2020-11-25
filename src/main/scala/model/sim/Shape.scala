package model.sim

import java.io.{ByteArrayInputStream, InputStream}
import java.util.UUID

import model.{Position, Positioned}

//TODO: This doesn't seem like it's needed anymore
sealed trait Shape

sealed trait Element extends Shape

object Shape {}

// Assume all parameters are the default generated by JSimGraph, we'll add parameters next week
case class Node(
    var name: String,
    var position: Position,
    nodeType: NodeType,
    var rotated: Boolean = false,
    uid: UUID = UUID.randomUUID()
) extends Element
    with Positioned {

  override def equals(obj: Any): Boolean =
    obj match {
      case node: Node => uid == node.uid
      case _ => false
    }

  override def hashCode: Int = uid.hashCode
}

//TODO: Refactor once done, maybe a NodeType file?

sealed trait NodeType

case class Source(
//    sourceSection: SourceSection,
//    tunnelSection: TunnelSection,
//    routerSection: RouterSection
) extends NodeType

case class Sink() extends NodeType

case class Terminal() extends NodeType

case class Router() extends NodeType

case class Delay() extends NodeType

//This is what you'd intuitively call a queue
case class Server() extends NodeType

case class Fork() extends NodeType

case class Join() extends NodeType

case class Logger() extends NodeType

case class ClassSwitch() extends NodeType

case class Semaphore() extends NodeType

case class Scalar() extends NodeType

case class Place() extends NodeType

case class Transition() extends NodeType

case class Unimplemented() extends NodeType

// Sections make up a NodeType
sealed trait TypeSection

case class SourceSection()      extends TypeSection
case class TunnelSection()      extends TypeSection
case class RouterSection()      extends TypeSection
case class SinkSection()        extends TypeSection
case class TerminalSection()    extends TypeSection
case class QueueSection()       extends TypeSection
case class DelaySection()       extends TypeSection
case class ServerSection()      extends TypeSection
case class ForkSection()        extends TypeSection
case class JoinSection()        extends TypeSection
case class LoggerSection()      extends TypeSection
case class ClassSwitchSection() extends TypeSection
case class SemaphoreSection()   extends TypeSection
case class StorageSection()     extends TypeSection
case class LinkageSection()     extends TypeSection
case class EnablingSection()    extends TypeSection
case class TimingSection()      extends TypeSection
case class FiringSection()      extends TypeSection

case class Connection(source: Node, target: Node) extends Element

case class Trace(
    var image: Trace.Image,
    var position: Position,
    var height: Int,
    var width: Int
) extends Shape
    with Positioned {
  def end: Position = position + Position(height, width)
  val uid: UUID     = UUID.randomUUID()

  override def equals(obj: Any): Boolean =
    obj match {
      case node: Node => uid == node.uid
      case _ => false
    }
  override def hashCode: Int             = uid.hashCode
}

object Trace        {
  /* Use a byte array for redrawing in memory, especially since a FileInputStream cannot be reset.
     A better buffer system may be warranted later, but we can keep the interface consistent and provide a stream
     as necessary */
  class Image(raw: InputStream) {
    private val bytes       = raw.readAllBytes()
    def stream: InputStream = { new ByteArrayInputStream(bytes) }

    override def equals(obj: Any): Boolean =
      obj match { case t: Image => eq(t); case _ => false }
  }

  object Image                  {
    def apply(raw: InputStream): Image = new Image(raw)

    def unapply(image: Image): Option[InputStream] = Some(image.stream)
  }
}
