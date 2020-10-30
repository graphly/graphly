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
import ui.canvas.{
  GraphCanvasContainer,
  GraphCanvasController,
  VerticalSettingsMenu
}

class AppMainSceneView(width: Double, height: Double)
    extends Scene(width, height) {
  private var model: Sim     = Sim.empty
  private val controller     = new GraphCanvasController(model)
  private val graphContainer = new GraphCanvasContainer(controller)
  private val rightMenu      = VerticalSettingsMenu(controller)

  private val statusBar =
    new Label() { text = s"Status: ${controller.mode.toolbarStatusMnemonic}" }
  controller.onSwitchMode +=
    (state => statusBar.text = s"Status: ${state.toolbarStatusMnemonic}")

  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("File") {
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
                val xmlFile     = fileChooser.showOpenDialog(new Stage)
                //TODO: Update display
                model = xml.XML.loadFile(xmlFile).toSim
              }
              accelerator =
                new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown)
            }
          )
        },
        new Menu("Edit") {
          items = List(new MenuItem("Select") {
            onAction = (_: ActionEvent) =>
              controller.redrawMode(
                GraphCanvasController.EditingMode.Selecting,
                graphContainer.canvas.redraw
              )
            accelerator =
              new KeyCodeCombination(KeyCode.M, KeyCombination.AltDown)
          })
        },
        new Menu("Draw") {
          items = List(
            new MenuItem("Source") {
              onAction = (_: ActionEvent) =>
                controller.redrawMode(
                  GraphCanvasController.EditingMode.Node(Source),
                  graphContainer.canvas.redraw
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
                  graphContainer.canvas.redraw
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
                  graphContainer.canvas.redraw
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
                  graphContainer.canvas.redraw
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
                  graphContainer.canvas.redraw
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
                  graphContainer.canvas.redraw
                )
              accelerator =
                new KeyCodeCombination(KeyCode.E, KeyCombination.AltDown)
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
