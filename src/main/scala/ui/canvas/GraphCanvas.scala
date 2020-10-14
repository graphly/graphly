package ui.canvas

import javafx.event.EventHandler
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.input.KeyCode
import scalafx.scene.paint.Color
import scalafx.scene.shape.ArcType

class GraphCanvas extends Canvas {

  object StateMachine extends Enumeration {
    val Node, Edge, EdgeStartDrawing = Value
  }

  private val gctx = graphicsContext2D
  private var shapes = List[Shape]()

  // Hacky shit
  var currentState = StateMachine.Node
  var startNode: Node = null

  onKeyPressed = e => {
    if (currentState == StateMachine.Node) {
      currentState = StateMachine.Edge
    } else {
      currentState = StateMachine.Node
    }

    println("Switching to state " + currentState.toString())
  }

  onMouseClicked = (e: MouseEvent) => {
    currentState match {
      case StateMachine.Node => shapes ::= new Node(e.getX, e.getY)
      case StateMachine.Edge => {
        hitTest(e.getX, e.getY) match {
          case node: Option[Node] => {
            startNode = node.get
            startNode.selected = true
            currentState = StateMachine.EdgeStartDrawing
          }
        }
      }
      case StateMachine.EdgeStartDrawing => {
        hitTest(e.getX, e.getY) match {
          case node: Option[Node] => {
            if (node.get != startNode) {
              startNode.selected = false
              shapes ::= new Edge(startNode, node.get)
              currentState = StateMachine.EdgeStartDrawing
            }
          }
          case None => {
            startNode.selected = false
            startNode = null
            currentState = StateMachine.Edge
          }
        }
      }
    }

    redraw()
  }

  onMouseMoved = (e: MouseEvent) => {
  }

  def redraw() = {
    gctx.fill = Color.White
    gctx.fillRect(0, 0, width.value, height.value)
    for (node <- shapes) node.Draw(gctx)
  }

  def hitTest(x: Double, y: Double): Option[Node] = {
    for (node <- shapes) node match {
      case node: Node => if (node.HitTest(x, y)) return Option.apply(node)
    }

    return Option.empty
  }
}