package ui.widgetPanel

import model.sim.{ClassSwitch, Delay, Disabled, DropStrategy, FastestService, Fork, JSQ, LeastUtilisation, Logger, PowerOfK, QueueSection, Random, RoundRobin, Router, RouterSection, RoutingStrategy, SRT, Server, Sim}
import ui.canvas.GraphCanvasController

class NodeWidget(title: String, model: Sim)
    extends PropertiesWidget(title, model) {
  def routerWidget(router: RouterSection): Unit = {
    val strategies = List[RoutingStrategy](
      Random(),
      RoundRobin(),
      JSQ(),
      SRT(),
      LeastUtilisation(),
      FastestService(),
      PowerOfK(),
      Disabled()
    )
    dropdown(
      "Routing Strategy",
      strategies.map(_.toString),
      router.routingStrategy.toString,
      (_, y) => {
        val oldStrat = router.routingStrategy
        router.routingStrategy = strategies.find(s => s.toString.equals(y)).get
        println(oldStrat, router.routingStrategy)
        oldStrat match {
          case _: PowerOfK => removeLastRows(2)
          case _ =>
        }
        router.routingStrategy match {
          case strat: PowerOfK =>
            integerField("K", strat.k, (_, k) => strat.k = k)
            checkbox("Has memory", strat.hasMemory, (_, hasMem) => strat.hasMemory = hasMem)
          case _ =>
        }
      }
    )
    router.routingStrategy match {
      case strat: PowerOfK =>
        integerField("K", strat.k, (_, k) => strat.k = k)
        checkbox("Has memory", strat.hasMemory, (_, hasMem) => strat.hasMemory = hasMem)
      case _ =>
    }
  }

  private def queueWidget(queue: QueueSection): Unit = {
    integerField(
      "Queue size",
      queue.size.getOrElse(-1),
      (_, y) => queue.size = Some(y)
    )
    dropdown(
      "Drop Strategy",
      DropStrategy.values.toList.map(_.toString),
      queue.dropStrategy.getOrElse(DropStrategy.BAS_BLOCKING).toString,
      (_, y) => queue.dropStrategy = Some(DropStrategy.withName(y))
    )
  }
}

object NodeWidget                          {
  def apply(
      title: String,
      model: Sim,
      graphController: GraphCanvasController[_]
  ): NodeWidget = {
    val widget = new NodeWidget(title, model)
    graphController.onSwitchMode += {
      case GraphCanvasController.EditingMode.SelectNode(nodes) =>
        if (nodes.size == 1) {
          widget.clear()
          widget.visible = true
          nodes.foreach(n => {
            widget.textField("Name", n.name, (_, y) => n.name = y)
            widget.checkbox("Rotated", n.rotated, (_, y) => n.rotated = y)
            n.nodeType match {
              case Server(queue, _, router) =>
                widget.queueWidget(queue)
                widget.routerWidget(router)
              case Router(queue, _, router) =>
                widget.queueWidget(queue)
                widget.routerWidget(router)
              case Delay(queue, _, _) => widget.queueWidget(queue)
              case Fork(queue, _, _) => widget.queueWidget(queue)
              case Logger(queue, _, _) => widget.queueWidget(queue)
              case ClassSwitch(queue, _, _) => widget.queueWidget(queue)
              case _ =>
            }
          })
        }
      case GraphCanvasController.EditingMode.DragNode(_, _, _) =>
      case _ => widget.visible = false
    }
    widget
  }
}
