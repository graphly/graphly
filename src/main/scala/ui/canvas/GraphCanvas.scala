package ui.canvas

import javafx.scene.input.MouseEvent
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color

class GraphCanvas extends Canvas {

  object StateMachine extends Enumeration {
    val Node, Edge, EdgeStartDrawing = Value
  }

  private val gctx = graphicsContext2D
  private var shapes = List[Shape]()

  // Hacky shit.
  private var currentState: StateMachine.Value = StateMachine.Node
  private var startNode: Option[Node] = None
  private var tempSelect: Option[Node] = None

  onMouseClicked = (e: MouseEvent) => {
    currentState match {
      case StateMachine.Node => shapes ::= new Node(e.getX, e.getY)
      case StateMachine.Edge =>
        hitTest(e.getX, e.getY) match {
          case node: Some[Node] =>
            startNode = node
            startNode.get.selected = true
            currentState = StateMachine.EdgeStartDrawing
          case None =>
        }
      case StateMachine.EdgeStartDrawing =>
        hitTest(e.getX, e.getY) match {
          case node: Some[Node] =>
            if (node != startNode) {
              shapes ::= new Edge(startNode.get, node.get)

              startNode.get.selected = false
              startNode = null

              currentState = StateMachine.Edge
            }
          case None =>
            startNode.get.selected = false
            startNode = null
            currentState = StateMachine.Edge
        }
    }

    redraw()
  }

  onMouseMoved = (e: MouseEvent) => {
    hitTest(e.getX, e.getY) match {
      case node: Some[Node] =>
        if (node != startNode) {
          tempSelect = node
          tempSelect.get.selected = true
          redraw()
        }
      case None =>
        if (tempSelect.isDefined && tempSelect != startNode) {
          tempSelect.get.selected = false
          tempSelect = None
          redraw()
        }
    }
  }

  def redraw(): Unit = {
    gctx.fill = Color.White
    gctx.fillRect(0, 0, width.value, height.value)
    for (node <- shapes) node.Draw(gctx)
  }

  def hitTest(x: Double, y: Double): Option[Node] = {
    for (node <- shapes) node match {
      case node: Node =>
        if (node.HitTest(x, y)) return Option.apply(node)
      case _ =>
    }

    return Option.empty
  }

  def drawingModeNodes(): Unit = {
    if (startNode.isDefined) {
      startNode.get.selected = false
      startNode = None
    }

    currentState = StateMachine.Node
    println("Drawing Nodes now")
  }

  def drawingModeEdges(): Unit = {
    currentState = StateMachine.Edge
    println("Drawing Edges now")
  }
}