package model.sim.defaults

import model.sim._
import util.Default

//TODO: This may not be needed
object Implicit {

  implicit object SinkDefault   extends Default[Sink]   {
    override def default: Sink = Sink(SinkSection())
  }

  implicit object ServerDefault extends Default[Server] {
    override def default: Server =
      Server(
        QueueSection(<section className="Queue">
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
        RouterSection(
          <parameter array="true" classPath="jmt.engine.NetStrategies.RoutingStrategy" name="RoutingStrategy">
          <refClass>Class1</refClass>
          <subParameter classPath="jmt.engine.NetStrategies.RoutingStrategies.RandomStrategy" name="Random"/>
        </parameter>
        )
      )
  }

  implicit object JoinDefault   extends Default[Join]   {
    override def default: Join =
      Join(
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
        TunnelSection(),
        RouterSection(
          <parameter array="true" classPath="jmt.engine.NetStrategies.RoutingStrategy" name="RoutingStrategy">
          <refClass>Class2</refClass>
          <subParameter classPath="jmt.engine.NetStrategies.RoutingStrategies.RandomStrategy" name="Random"/>
        </parameter>
        )
      )
  }

  implicit object SourceDefault extends Default[Source] {
    override def default: Source =
      Source(
        SourceSection(Seq.empty),
        TunnelSection(),
        RouterSection(
          <parameter array="true" classPath="jmt.engine.NetStrategies.RoutingStrategy" name="RoutingStrategy">
          <refClass>Class1</refClass>
          <subParameter classPath="jmt.engine.NetStrategies.RoutingStrategies.RandomStrategy" name="Random"/>
        </parameter>
        )
      )
  }

  implicit object ForkDefault   extends Default[Fork]   {
    override def default: Fork =
      Fork(
        QueueSection(<section className="Queue">
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
        TunnelSection(),
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
      )
  }

}
