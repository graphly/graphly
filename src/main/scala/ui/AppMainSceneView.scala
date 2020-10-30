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
import ui.canvas.{GraphCanvasController, GraphingCanvas, VerticalSettingsMenu}

class AppMainSceneView(width: Double, height: Double)
    extends Scene(width, height) {
  private var model: Sim     = Sim.empty
  private val controller     =
    new GraphCanvasController[GraphingCanvas.DrawAction](model)
  private val graphContainer = new GraphingCanvas(controller)
  private val rightMenu      = VerticalSettingsMenu(controller)

  private val statusBar =
    new Label() { text = s"Status: ${controller.mode.toolbarStatusMnemonic}" }
  controller.onSwitchMode +=
    (state => statusBar.text = s"Status: ${state.toolbarStatusMnemonic}")

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
          items = List(new MenuItem("Select") {
            onAction = (_: ActionEvent) =>
              controller.redrawMode(
                GraphCanvasController.EditingMode.Selecting,
                graphContainer.redraw
              )
            accelerator =
              new KeyCodeCombination(KeyCode.M, KeyCombination.AltDown)
          })
        },
        new Menu("Draw")  {
          items = List(
            new MenuItem("Source") {
              onAction = (_: ActionEvent) =>
                controller.redrawMode(
                  GraphCanvasController.EditingMode.Node(Source),
                  graphContainer.redraw
                )
              accelerator = new KeyCodeCombination(
                KeyCode.N,
                KeyCombination.ShiftDown,
                KeyCombination.ControlDown
              )
            },
            new MenuItem("Fork")   {
              onAction = (_: ActionEvent) =>
                controller.redrawMode(
                  GraphCanvasController.EditingMode.Node(Fork),
                  graphContainer.redraw
                )
              accelerator = new KeyCodeCombination(
                KeyCode.F,
                KeyCombination.ShiftDown,
                KeyCombination.ControlDown
              )
            },
            new MenuItem("Join")   {
              onAction = (_: ActionEvent) =>
                controller.redrawMode(
                  GraphCanvasController.EditingMode.Node(Join),
                  graphContainer.redraw
                )
              accelerator = new KeyCodeCombination(
                KeyCode.J,
                KeyCombination.ShiftDown,
                KeyCombination.AltDown
              )
            },
            new MenuItem("Queue")  {
              onAction = (_: ActionEvent) =>
                controller.redrawMode(
                  GraphCanvasController.EditingMode.Node(Queue),
                  graphContainer.redraw
                )
              accelerator = new KeyCodeCombination(
                KeyCode.Q,
                KeyCombination.ShiftDown,
                KeyCombination.AltDown
              )
            },
            new MenuItem("Sink")   {
              onAction = (_: ActionEvent) =>
                controller.redrawMode(
                  GraphCanvasController.EditingMode.Node(Sink),
                  graphContainer.redraw
                )
              accelerator = new KeyCodeCombination(
                KeyCode.S,
                KeyCombination.ShiftDown,
                KeyCombination.AltDown
              )
            },
            new SeparatorMenuItem(),
            new MenuItem("Edges")  {
              onAction = (_: ActionEvent) =>
                controller.redrawMode(
                  GraphCanvasController.EditingMode.BeginEdge,
                  graphContainer.redraw
                )
              accelerator =
                new KeyCodeCombination(KeyCode.E, KeyCombination.AltDown)
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
  }
}
