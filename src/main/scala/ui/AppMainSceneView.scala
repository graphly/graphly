package ui

import java.io.File
import io.Implicit.SimableRepresention
import io.XMLSimRepresentation.Implicit.xmlSimRepresentation
import javafx.event.ActionEvent
import javafx.scene.input.KeyEvent
import model.sim._
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.BorderPane
import scalafx.stage.{FileChooser, Stage}
import ui.canvas.SimDrawAction._
import ui.canvas.widgetPanel.{NodeWidget, PropertiesWidget, WidgetPanel}
import ui.canvas.{GraphCanvasController, GraphingCanvas}
import ui.toolbar.VerticalToolbar

class AppMainSceneView(width: Double, height: Double)
    extends Scene(width, height) {
  private var model: Sim     = Sim.empty
  private val controller     =
    new GraphCanvasController[GraphingCanvas.DrawAction](model)
  private val graphContainer = new GraphingCanvas(controller)
  private val rightMenu      = WidgetPanel.Element()
  private val general        = new PropertiesWidget("Model Settings", model)
  private val nodeMenu       = NodeWidget("Node Settings", model, controller)
  nodeMenu.visible = false

  def hasChanges: Boolean         = controller.hasChanges
  def checkUserWantsExit: Boolean = controller.checkUserWantsExit

  private def resetGeneralMenu(mdl: Sim): Unit = {
    val newGeneral = new PropertiesWidget("Model Settings", mdl)
    newGeneral.generateGlobalMenu()
    rightMenu.children.set(0, newGeneral)
  }

  private def setModel(mdl: Sim): Unit         = {
    model = mdl
    controller.updateModel(model)
    controller.redrawMode(
      GraphCanvasController.EditingMode.Selecting,
      graphContainer.redraw
    )
    resetGeneralMenu(model)
  }

  // This commented code is only used to generate the general simulation config fields, and should be deleted
  // TODO: remove once done
//  private val fields = model.configuration.getClass.getDeclaredFields
//  private val jLong = classOf[Long]
//  private val jInt = classOf[Int]
//  private val jString = classOf[String]
//  private val jBool = classOf[Boolean]
//  private val jDouble = classOf[Double]
//
//  def convertCamelCase(str: String): String = {
//    var temp = str.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").replaceAll("([a-z\\d])([A-Z])", "$1_$2").toLowerCase
//    temp.replaceAll("_", " ").capitalize
//  }
//
//  fields.foreach {
//    f => f.getType match {
//      case `jLong` | `jInt` => println(s"""integerField("${convertCamelCase(f.getName)}", model.configuration.${f.getName}, _.${f.getName} = _)""")
//      case `jString` => println(s"""textField("${convertCamelCase(f.getName)}", model.configuration.${f.getName}, _.${f.getName} = _)""")
//      case `jBool` => println(s"""checkbox("${convertCamelCase(f.getName)}", model.configuration.${f.getName}, _.${f.getName} = _)""")
//      case `jDouble` => println(s"""doubleField("${convertCamelCase(f.getName)}", model.configuration.${f.getName}, _.${f.getName} = _)""")
//      case _ => println("what")
//    }
//  }

  general.generateGlobalMenu()
  rightMenu.widget(general)
  rightMenu.widget(nodeMenu)
  rightMenu.prefWidth <== graphContainer.width / 3
  private val toolbar = new VerticalToolbar

  private def niceFileString(fileOpt: Option[File]): String =
    fileOpt match {
      case Some(file) => file.getName
      case _ => "none"
    }

  private val statusBar = new Label() {
    text =
      s"Status: ${controller.mode.toolbarStatusMnemonic}, file: ${niceFileString(controller.editingFile)}"
  }
  controller.onSwitchMode +=
    (
        state =>
          statusBar.text =
            s"Status: ${state.toolbarStatusMnemonic}, file: ${niceFileString(controller.editingFile)}"
    )
  controller.onSwitchMode += (state => toolbar.controllerUpdatedMode(state))
  controller.onCanvasTransform +=
    (tuple => graphContainer.transformCanvas(tuple._1, tuple._2))

  toolbar.itemSelected +=
    (state => controller.redrawMode(state, graphContainer.redraw))

  root = new BorderPane {
    top = new MenuBar {
      menus = List(
        new Menu("File")  {
          items = List(
            new MenuItem("New")     {
              onAction = (_: ActionEvent) => {
                if (!hasChanges || checkUserWantsExit) {
                  setModel(Sim.empty)
                  controller.editingFile = Option.empty
                }
              }
              accelerator =
                new KeyCodeCombination(KeyCode.N, KeyCombination.ControlDown)
            },
            new MenuItem("Save")    {
              onAction = (_: ActionEvent) => {
                if (controller.editingFile.isEmpty) { controller.saveAs() }
                else { controller.save() }
              }
              accelerator =
                new KeyCodeCombination(KeyCode.S, KeyCombination.ControlDown)
            },
            new MenuItem("Save As") {
              onAction = (_: ActionEvent) => { controller.saveAs() }
              accelerator = new KeyCodeCombination(
                KeyCode.S,
                KeyCombination.ControlDown,
                KeyCombination.ShiftDown
              )
            },
            new SeparatorMenuItem(),
            new MenuItem("Open")    {
              onAction = (_: ActionEvent) => {
                if (!hasChanges || checkUserWantsExit) {
                  val fileChooser = new FileChooser()
                  fileChooser.initialDirectory =
                    new File(System.getProperty("user.home"))
                  fileChooser.title = "Open Simulation"
                  fileChooser.extensionFilters.add(
                    new FileChooser.ExtensionFilter("JSIMgraph XML", "*.jsimg")
                  )
                  fileChooser.showOpenDialog(new Stage) match {
                    case file: File =>
                      try { model = xml.XML.loadFile(file).toSim }
                      catch {
                        case _: Exception =>
                          controller.showError(
                            "Error",
                            "Error loading file.",
                            s"Failed to load file."
                          )
                      }
                      controller.editingFile = Some(file)
                      setModel(model)
                    case _ =>
                  }
                }
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
                new KeyCodeCombination(KeyCode.V, KeyCombination.ControlDown)
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

    rightMenu.onKeyPressed = (key: KeyEvent) => {
      if (key.getCode.equals(javafx.scene.input.KeyCode.ENTER)) {
        controller.redrawMode(controller.mode, graphContainer.redraw)
        rightMenu.requestFocus()
      }
    }
  }
}
