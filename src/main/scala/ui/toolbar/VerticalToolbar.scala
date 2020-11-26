package ui.toolbar

import model.sim._
import scalafx.geometry.Orientation
import scalafx.scene.control.{Separator, ToggleGroup, ToolBar}
import ui.canvas.{GraphCanvasController => GCC}
import ui.util.Event

class VerticalToolbar extends ToolBar {
  orientation = Orientation.Vertical

  // TODO Refactor state machine so that State -> EntryState.
  val itemSelected = new Event[GCC.EditingMode.State]

  private val allButtonsTg                                     = new ToggleGroup()
  private val selectBtn                                        =
    new ToolbarButton("Select Nodes or Edges", "assets/icons/select-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.Selecting) }
    }
  private val sourceBtn                                        =
    new ToolbarButton("Create Source Nodes", "assets/icons/source-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GCC.EditingMode.Node(Source(
          UnimplementedSection(<section className="RandomSource">
              <parameter array="true" classPath="jmt.engine.NetStrategies.ServiceStrategy" name="ServiceStrategy">
                <refClass>Class1</refClass>
                <subParameter classPath="jmt.engine.NetStrategies.ServiceStrategies.ServiceTimeStrategy" name="ServiceTimeStrategy">
                  <subParameter classPath="jmt.engine.random.Exponential" name="Exponential"/>
                  <subParameter classPath="jmt.engine.random.ExponentialPar" name="distrPar">
                    <subParameter classPath="java.lang.Double" name="lambda">
                      <value>0.5</value>
                    </subParameter>
                  </subParameter>
                </subParameter>
              </parameter>
            </section>),
          UnimplementedSection(<section className="ServiceTunnel"/>),
          UnimplementedSection(<section className="Router">
              <parameter array="true" classPath="jmt.engine.NetStrategies.RoutingStrategy" name="RoutingStrategy">
                <refClass>Class1</refClass>
                <subParameter classPath="jmt.engine.NetStrategies.RoutingStrategies.RandomStrategy" name="Random"/>
              </parameter>
            </section>)
        )))
      }
    }
  private val forkBtn                                          =
    new ToolbarButton("Create Fork Nodes", "assets/icons/fork-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GCC.EditingMode.Node(Fork(
          UnimplementedSection(<section className="Queue">
              <parameter classPath="java.lang.Integer" name="size">
                <value>-1</value>
              </parameter>
              <parameter array="true" classPath="java.lang.String" name="dropStrategies">
                <refClass>Class1</refClass>
                <subParameter classPath="java.lang.String" name="dropStrategy">
                  <value>drop</value>
                </subParameter>
              </parameter>
              <parameter classPath="jmt.engine.NetStrategies.QueueGetStrategies.FCFSstrategy" name="FCFSstrategy"/>
              <parameter array="true" classPath="jmt.engine.NetStrategies.QueuePutStrategy" name="QueuePutStrategy">
                <refClass>Class1</refClass>
                <subParameter classPath="jmt.engine.NetStrategies.QueuePutStrategies.TailStrategy" name="TailStrategy"/>
              </parameter>
            </section>),
          UnimplementedSection(<section className="ServiceTunnel"/>),
          UnimplementedSection(<section className="Fork">
              <parameter classPath="java.lang.Integer" name="jobsPerLink">
                <value>1</value>
              </parameter>
              <parameter classPath="java.lang.Integer" name="block">
                <value>-1</value>
              </parameter>
              <parameter classPath="java.lang.Boolean" name="isSimplifiedFork">
                <value>true</value>
              </parameter>
              <parameter array="true" classPath="jmt.engine.NetStrategies.ForkStrategy" name="ForkStrategy">
                <refClass>Class1</refClass>
                <subParameter classPath="jmt.engine.NetStrategies.ForkStrategies.ProbabilitiesFork" name="Branch Probabilities">
                  <subParameter array="true" classPath="jmt.engine.NetStrategies.ForkStrategies.OutPath" name="EmpiricalEntryArray">
                    <subParameter classPath="jmt.engine.NetStrategies.ForkStrategies.OutPath" name="OutPathEntry">
                      <subParameter classPath="jmt.engine.random.EmpiricalEntry" name="outUnitProbability">
                        <subParameter classPath="java.lang.String" name="stationName">
                          <value>Queue 1</value>
                        </subParameter>
                        <subParameter classPath="java.lang.Double" name="probability">
                          <value>1.0</value>
                        </subParameter>
                      </subParameter>
                      <subParameter array="true" classPath="jmt.engine.random.EmpiricalEntry" name="JobsPerLinkDis">
                        <subParameter classPath="jmt.engine.random.EmpiricalEntry" name="EmpiricalEntry">
                          <subParameter classPath="java.lang.String" name="numbers">
                            <value>1</value>
                          </subParameter>
                          <subParameter classPath="java.lang.Double" name="probability">
                            <value>1.0</value>
                          </subParameter>
                        </subParameter>
                      </subParameter>
                    </subParameter>
                    <subParameter classPath="jmt.engine.NetStrategies.ForkStrategies.OutPath" name="OutPathEntry">
                      <subParameter classPath="jmt.engine.random.EmpiricalEntry" name="outUnitProbability">
                        <subParameter classPath="java.lang.String" name="stationName">
                          <value>Semaphore 1</value>
                        </subParameter>
                        <subParameter classPath="java.lang.Double" name="probability">
                          <value>1.0</value>
                        </subParameter>
                      </subParameter>
                      <subParameter array="true" classPath="jmt.engine.random.EmpiricalEntry" name="JobsPerLinkDis">
                        <subParameter classPath="jmt.engine.random.EmpiricalEntry" name="EmpiricalEntry">
                          <subParameter classPath="java.lang.String" name="numbers">
                            <value>1</value>
                          </subParameter>
                          <subParameter classPath="java.lang.Double" name="probability">
                            <value>1.0</value>
                          </subParameter>
                        </subParameter>
                      </subParameter>
                    </subParameter>
                  </subParameter>
                </subParameter>
              </parameter>
            </section>)
        )))
      }
    }
  private val joinBtn                                          =
    new ToolbarButton("Create Join Nodes", "assets/icons/source-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GCC.EditingMode.Node(Join(
          UnimplementedSection(<section className="Join">
            <parameter array="true" classPath="jmt.engine.NetStrategies.JoinStrategy" name="JoinStrategy">
              <refClass>Class2</refClass>
              <subParameter classPath="jmt.engine.NetStrategies.JoinStrategies.NormalJoin" name="Standard Join">
                <subParameter classPath="java.lang.Integer" name="numRequired">
                  <value>-1</value>
                </subParameter>
              </subParameter>
            </parameter>
          </section>),
          UnimplementedSection(<section className="ServiceTunnel"/>),
          UnimplementedSection(<section className="Router">
            <parameter array="true" classPath="jmt.engine.NetStrategies.RoutingStrategy" name="RoutingStrategy">
              <refClass>Class2</refClass>
              <subParameter classPath="jmt.engine.NetStrategies.RoutingStrategies.RandomStrategy" name="Random"/>
            </parameter>
          </section>)
        )))
      }
    }
  private val queueBtn                                         =
    new ToolbarButton("Create Queue Nodes", "assets/icons/queue-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GCC.EditingMode.Node(Server(
          UnimplementedSection(<section className="Queue">
            <parameter classPath="java.lang.Integer" name="size">
              <value>-1</value>
            </parameter>
            <parameter array="true" classPath="java.lang.String" name="dropStrategies">
              <refClass>Class1</refClass>
              <subParameter classPath="java.lang.String" name="dropStrategy">
                <value>drop</value>
              </subParameter>
            </parameter>
            <parameter classPath="jmt.engine.NetStrategies.QueueGetStrategies.FCFSstrategy" name="FCFSstrategy"/>
            <parameter array="true" classPath="jmt.engine.NetStrategies.QueuePutStrategy" name="QueuePutStrategy">
              <refClass>Class1</refClass>
              <subParameter classPath="jmt.engine.NetStrategies.QueuePutStrategies.TailStrategy" name="TailStrategy"/>
            </parameter>
          </section>),
          UnimplementedSection(<section className="Server">
              <parameter classPath="java.lang.Integer" name="maxJobs">
                <value>1</value>
              </parameter>
              <parameter array="true" classPath="java.lang.Integer" name="numberOfVisits">
                <refClass>Class1</refClass>
                <subParameter classPath="java.lang.Integer" name="numberOfVisits">
                  <value>1</value>
                </subParameter>
              </parameter>
              <parameter array="true" classPath="jmt.engine.NetStrategies.ServiceStrategy" name="ServiceStrategy">
                <refClass>Class1</refClass>
                <subParameter classPath="jmt.engine.NetStrategies.ServiceStrategies.ServiceTimeStrategy" name="ServiceTimeStrategy">
                  <subParameter classPath="jmt.engine.random.Exponential" name="Exponential"/>
                  <subParameter classPath="jmt.engine.random.ExponentialPar" name="distrPar">
                    <subParameter classPath="java.lang.Double" name="lambda">
                      <value>1.0</value>
                    </subParameter>
                  </subParameter>
                </subParameter>
              </parameter>
            </section>),
          UnimplementedSection(<section className="Router">
            <parameter array="true" classPath="jmt.engine.NetStrategies.RoutingStrategy" name="RoutingStrategy">
              <refClass>Class1</refClass>
              <subParameter classPath="jmt.engine.NetStrategies.RoutingStrategies.RandomStrategy" name="Random"/>
            </parameter>
          </section>)
        )))
      }
    }
  private val sinkBtn                                          =
    new ToolbarButton("Create Sink Nodes", "assets/icons/sink-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => {
        itemSelected.dispatch(GCC.EditingMode.Node(Sink(
          UnimplementedSection(<section className="JobSink"/>)
        )))
      }
    }
  private val edgesBtn                                         =
    new ToolbarButton("Create Edges", "assets/icons/edge-32.png") {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.BeginEdge) }
    }

  items = List(
    selectBtn,
    new Separator(),
    sourceBtn,
    forkBtn,
    joinBtn,
    queueBtn,
    sinkBtn,
    new Separator(),
    edgesBtn
  )
  /*
    new Separator(),
    new ToolbarButton("Add Trace", "assets/icons/trace-add-32.png")          {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.) }
    },
    new ToolbarButton("Select Trace", "assets/icons/select-32.png")          {
      toggleGroup = allButtonsTg
      onAction = e => { itemSelected.dispatch(GCC.EditingMode.SelectingTrace) }
    }
   */

  def controllerUpdatedMode(mode: GCC.EditingMode.State): Unit = {
    mode match {
      case GCC.EditingMode.Selecting => selectBtn.selected = true
      case _ =>
    }
  }
}
