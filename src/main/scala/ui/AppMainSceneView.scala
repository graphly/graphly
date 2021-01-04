package ui

import java.io.File

import io.Implicit.SimableRepresention
import io.XMLSimRepresentation.Implicit.xmlSimRepresentation
import javafx.event.ActionEvent
import model.sim._
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.BorderPane
import scalafx.stage.{FileChooser, Stage}
import ui.canvas.SimDrawAction._
import ui.canvas.widgetPanel.WidgetPanel
import ui.canvas.{GraphCanvasController, GraphingCanvas}
import ui.toolbar.VerticalToolbar

class AppMainSceneView(width: Double, height: Double)
    extends Scene(width, height) {
  private var model: Sim     = Sim.empty
  private val controller     =
    new GraphCanvasController[GraphingCanvas.DrawAction](model)
  private val graphContainer = new GraphingCanvas(controller)
  private val rightMenu      = WidgetPanel.Element(controller)
  rightMenu.prefWidth <== graphContainer.width / 3
  private val toolbar        = new VerticalToolbar

  private val statusBar =
    new Label() { text = s"Status: ${controller.mode.toolbarStatusMnemonic}" }
  controller.onSwitchMode +=
    (state => statusBar.text = s"Status: ${state.toolbarStatusMnemonic}")
  controller.onSwitchMode +=
    (state => toolbar.controllerUpdatedMode(state))
  controller.onCanvasTransform +=
    (tuple => graphContainer.transformCanvas(tuple._1, tuple._2))

  toolbar.itemSelected +=
    (state => controller.redrawMode(state, graphContainer.redraw))

  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("File")  {
          items = List(
            new MenuItem("Save")    {
              onAction = (_: ActionEvent) => { controller.save() }
              accelerator =
                new KeyCodeCombination(KeyCode.S, KeyCombination.ControlDown)
            },
            new MenuItem("Save As") {
              disable = true
              accelerator = new KeyCodeCombination(
                KeyCode.S,
                KeyCombination.ControlDown,
                KeyCombination.AltDown
              )
            },
            new SeparatorMenuItem(),
            new MenuItem("Open")    {
              onAction = (_: ActionEvent) => {
                val fileChooser = new FileChooser()
                fileChooser.initialDirectory =
                  new File(System.getProperty("user.home"))
                fileChooser.title = "Open Simulation"
                fileChooser.extensionFilters.add(
                  new FileChooser.ExtensionFilter("JSIMgraph XML", "*.jsimg")
                )
                Option(fileChooser.showOpenDialog(new Stage)).foreach { file =>
                  model = xml.XML.loadFile(file).toSim
                }
                //TODO: Update display
                controller.model = model
                controller.redrawMode(
                  GraphCanvasController.EditingMode.Selecting,
                  graphContainer.redraw
                )
              }
              accelerator =
                new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown)
            }
          )
        },
        new Menu("Edit")  {
          items = List(
            new MenuItem("Delete") {
              onAction = (_: ActionEvent) =>
                controller.deleteSelected(graphContainer.redraw)
              accelerator = new KeyCodeCombination(KeyCode.Delete)
            },
            new SeparatorMenuItem(),
            new MenuItem("Copy")   {
              onAction = (_: ActionEvent) =>
                controller.copySelectedNodes(graphContainer.redraw)
              accelerator =
                new KeyCodeCombination(KeyCode.C, KeyCombination.ControlDown)
            },
            new MenuItem("Paste")  {
              onAction = (_: ActionEvent) =>
                controller.pasteSelectedNodes(graphContainer.redraw)
              accelerator =
                new KeyCodeCombination(KeyCode.P, KeyCombination.ControlDown)
            },
            new MenuItem("Cut")    {
              onAction = (_: ActionEvent) =>
                controller.cutSelectedNodes(graphContainer.redraw)
              accelerator =
                new KeyCodeCombination(KeyCode.X, KeyCombination.ControlDown)
            },
            new MenuItem("Undo")   {
              onAction =
                (_: ActionEvent) => controller.undo(graphContainer.redraw)
              accelerator =
                new KeyCodeCombination(KeyCode.Z, KeyCombination.ControlDown)
            },
            new MenuItem("Redo")   {
              onAction =
                (_: ActionEvent) => controller.redo(graphContainer.redraw)
              accelerator = new KeyCodeCombination(
                KeyCode.Z,
                KeyCombination.ControlDown,
                KeyCombination.ShiftDown
              )
            }
          )
        },
        new Menu("Trace") {
          items = List(
            new MenuItem("Select") {
              onAction = _ =>
                controller.redrawMode(
                  GraphCanvasController.EditingMode.SelectingTrace,
                  graphContainer.redraw
                )
              accelerator = new KeyCodeCombination(
                KeyCode.T,
                KeyCombination.AltDown,
                KeyCombination.ShiftDown
              )
            },
            new MenuItem("Insert") {
              onAction = _ => controller.loadTrace(graphContainer.redraw)
              accelerator = new KeyCodeCombination(
                KeyCode.T,
                KeyCombination.ControlDown,
                KeyCombination.ShiftDown
              )
            }
          )
        }
      )
    }

    center = graphContainer

    bottom = statusBar
    right = rightMenu
    left = toolbar

    rightMenu.managed.onChange {
      controller.redrawMode(controller.mode, graphContainer.redraw)
    }
  }
}
