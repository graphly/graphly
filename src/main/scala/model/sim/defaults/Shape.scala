package model.sim.defaults

import model.sim._
import util.Default

import scala.collection.mutable

object Implicit {

  implicit object SinkDefault             extends Default[Sink]             {
    override def default: Sink = Sink(Default.default(SinkSectionDefault))
  }

  implicit object ServerDefault           extends Default[Server]           {
    override def default: Server =
      Server(
        Default.default(QueueSectionDefault),
        UnimplementedSection(<section className="Server">
          <parameter classPath="java.lang.Integer" name="maxJobs">
            <value>1</value>
          </parameter>
          <parameter array="true" classPath="java.lang.Integer" name="numberOfVisits"/>
          <parameter array="true" classPath="jmt.engine.NetStrategies.ServiceStrategy" name="ServiceStrategy"/>
        </section>),
        Default.default(RouterSectionDefault)
      )
  }

  implicit object JoinDefault             extends Default[Join]             {
    override def default: Join =
      Join(
        Default.default(JoinSectionDefault),
        Default.default(TunnelSectionDefault),
        Default.default(RouterSectionDefault)
      )
  }

  implicit object SourceDefault           extends Default[Source]           {
    override def default: Source =
      Source(
        Default.default(SourceSectionDefault),
        Default.default(TunnelSectionDefault),
        Default.default(RouterSectionDefault)
      )
  }

  implicit object ForkDefault             extends Default[Fork]             {
    override def default: Fork =
      Fork(
        Default.default(QueueSectionDefault),
        Default.default(TunnelSectionDefault),
        Default.default(ForkSectionDefault)
      )
  }

  implicit object TerminalDefault         extends Default[Terminal]         {
    override def default: Terminal = ???
  }

  implicit object RouterDefault           extends Default[Router]           {
    override def default: Router =
      Router(
        Default.default(QueueSectionDefault),
        Default.default(TunnelSectionDefault),
        Default.default(RouterSectionDefault)
      )
  }

  implicit object DelayDefault            extends Default[Delay]            {
    override def default: Delay =
      Delay(
        Default.default(QueueSectionDefault),
        UnimplementedSection(<section className="Delay">
          <parameter array="true" classPath="jmt.engine.NetStrategies.ServiceStrategy" name="ServiceStrategy"/>
        </section>),
        Default.default(RouterSectionDefault)
      )
  }

  implicit object LoggerDefault           extends Default[Logger]           {
    override def default: Logger =
      Logger(
        Default.default(QueueSectionDefault),
        UnimplementedSection(<section className="LogTunnel">
            <parameter classPath="java.lang.String" name="logfileName">
              <value>global.csv</value>
            </parameter>
            <parameter classPath="java.lang.String" name="logfilePath">
              <value>~/JMT/</value>
            </parameter>
            <parameter classPath="java.lang.Boolean" name="logExecTimestamp">
              <value>false</value>
            </parameter>
            <parameter classPath="java.lang.Boolean" name="logLoggerName">
              <value>true</value>
            </parameter>
            <parameter classPath="java.lang.Boolean" name="logTimeStamp">
              <value>true</value>
            </parameter>
            <parameter classPath="java.lang.Boolean" name="logJobID">
              <value>true</value>
            </parameter>
            <parameter classPath="java.lang.Boolean" name="logJobClass">
              <value>false</value>
            </parameter>
            <parameter classPath="java.lang.Boolean" name="logTimeSameClass">
              <value>false</value>
            </parameter>
            <parameter classPath="java.lang.Boolean" name="logTimeAnyClass">
              <value>false</value>
            </parameter>
            <parameter classPath="java.lang.Integer" name="numClasses">
              <value>0</value>
            </parameter>
          </section>),
        Default.default(RouterSectionDefault)
      )
  }

  implicit object ClassSwitchDefault      extends Default[ClassSwitch]      {
    override def default: ClassSwitch =
      ClassSwitch(
        Default.default(QueueSectionDefault),
        UnimplementedSection(<section className="ClassSwitch">
          <parameter array="true" classPath="java.lang.Object" name="matrix"/>
        </section>),
        Default.default(RouterSectionDefault)
      )
  }

  implicit object SemaphoreDefault        extends Default[Semaphore]        {
    override def default: Semaphore =
      Semaphore(
        UnimplementedSection(<section className="Semaphore">
      <parameter array="true" classPath="jmt.engine.NetStrategies.SemaphoreStrategy" name="SemaphoreStrategy"/>
    </section>),
        Default.default(TunnelSectionDefault),
        Default.default(RouterSectionDefault)
      )
  }

  implicit object ScalarDefault           extends Default[Scalar]           {
    override def default: Scalar =
      Scalar(
        Default.default(JoinSectionDefault),
        Default.default(TunnelSectionDefault),
        Default.default(ForkSectionDefault)
      )
  }

  implicit object PlaceDefault            extends Default[Place]            {
    override def default: Place =
      Place(
        UnimplementedSection(<section className="Storage">
      <parameter classPath="java.lang.Integer" name="totalCapacity">
        <value>-1</value>
      </parameter>
      <parameter array="true" classPath="java.lang.Integer" name="capacities"/>
      <parameter array="true" classPath="java.lang.String" name="dropRules"/>
      <parameter classPath="jmt.engine.NetStrategies.QueueGetStrategies.FCFSstrategy" name="getStrategy"/>
      <parameter array="true" classPath="jmt.engine.NetStrategies.QueuePutStrategy" name="putStrategies"/>
    </section>),
        Default.default(TunnelSectionDefault),
        UnimplementedSection(<section className="Linkage"/>)
      )
  }

  implicit object TransitionDefault       extends Default[Transition]       {
    override def default: Transition =
      Transition(UnimplementedSection(<section className="Enabling">
      <parameter array="true" classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionMatrix" name="enablingConditions">
        <subParameter classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionMatrix" name="enablingCondition">
          <subParameter array="true" classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionVector" name="enablingVectors"/>
        </subParameter>
      </parameter>
      <parameter array="true" classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionMatrix" name="inhibitingConditions">
        <subParameter classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionMatrix" name="inhibitingCondition">
          <subParameter array="true" classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionVector" name="inhibitingVectors"/>
        </subParameter>
      </parameter>
    </section>), UnimplementedSection(<section className="Timing">
        <parameter array="true" classPath="java.lang.String" name="modeNames">
          <subParameter classPath="java.lang.String" name="modeName">
            <value>Mode1</value>
          </subParameter>
        </parameter>
        <parameter array="true" classPath="java.lang.Integer" name="numbersOfServers">
          <subParameter classPath="java.lang.Integer" name="numberOfServers">
            <value>-1</value>
          </subParameter>
        </parameter>
        <parameter array="true" classPath="jmt.engine.NetStrategies.ServiceStrategy" name="timingStrategies">
          <subParameter classPath="jmt.engine.NetStrategies.ServiceStrategies.ServiceTimeStrategy" name="timingStrategy">
            <subParameter classPath="jmt.engine.random.Exponential" name="Exponential"/>
            <subParameter classPath="jmt.engine.random.ExponentialPar" name="distrPar">
              <subParameter classPath="java.lang.Double" name="lambda">
                <value>1.0</value>
              </subParameter>
            </subParameter>
          </subParameter>
        </parameter>
        <parameter array="true" classPath="java.lang.Integer" name="firingPriorities">
          <subParameter classPath="java.lang.Integer" name="firingPriority">
            <value>-1</value>
          </subParameter>
        </parameter>
        <parameter array="true" classPath="java.lang.Double" name="firingWeights">
          <subParameter classPath="java.lang.Double" name="firingWeight">
            <value>1.0</value>
          </subParameter>
        </parameter>
      </section>), UnimplementedSection(<section className="Firing">
        <parameter array="true" classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionMatrix" name="firingOutcomes">
          <subParameter classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionMatrix" name="firingOutcome">
            <subParameter array="true" classPath="jmt.engine.NetStrategies.TransitionUtilities.TransitionVector" name="firingVectors"/>
          </subParameter>
        </parameter>
      </section>))
  }

  implicit object SourceSectionDefault    extends Default[SourceSection]    {
    override def default: SourceSection = SourceSection(Seq.empty)
  }
  implicit object TunnelSectionDefault    extends Default[TunnelSection]    {
    override def default: TunnelSection = TunnelSection()
  }
  implicit object RouterSectionDefault    extends Default[RouterSection]    {
    override def default: RouterSection = RouterSection(Random())
  }
  implicit object SinkSectionDefault      extends Default[SinkSection]      {
    override def default: SinkSection = SinkSection()
  }
  implicit object TerminalSectionDefault  extends Default[TerminalSection]  {
    override def default: TerminalSection = TerminalSection()
  }
  implicit object QueueSectionDefault     extends Default[QueueSection]     {
    override def default: QueueSection =
      QueueSection(
        None,
        Some(DropStrategy.DROP),
        <parameter classPath="jmt.engine.NetStrategies.QueueGetStrategies.FCFSstrategy" name="FCFSstrategy"/>
        <parameter array="true" classPath="jmt.engine.NetStrategies.QueuePutStrategy" name="QueuePutStrategy"/>
      )
  }
  implicit object DelaySectionDefault     extends Default[DelaySection]     {
    override def default: DelaySection = DelaySection()
  }
  implicit object ServerSectionDefault    extends Default[ServerSection]    {
    override def default: ServerSection = ServerSection()
  }
  implicit object ForkSectionDefault      extends Default[ForkSection]      {
    override def default: ForkSection =
      ForkSection(
        1,
        true,
        <parameter classPath="java.lang.Integer" name="block">
      <value>-1</value>
    </parameter>
            <parameter array="true" classPath="jmt.engine.NetStrategies.ForkStrategy" name="ForkStrategy"/>
      )
  }
  implicit object JoinSectionDefault      extends Default[JoinSection]      {
    override def default: JoinSection = JoinSection(mutable.Map.empty)
  }
  implicit object LoggerSectionDefault    extends Default[LoggerSection]    {
    override def default: LoggerSection = LoggerSection()
  }
  implicit object ClassSwitchSectionDefault
      extends Default[ClassSwitchSection] {
    override def default: ClassSwitchSection = ClassSwitchSection()
  }
  implicit object SemaphoreSectionDefault extends Default[SemaphoreSection] {
    override def default: SemaphoreSection = SemaphoreSection()
  }
  implicit object StorageSectionDefault   extends Default[StorageSection]   {
    override def default: StorageSection = StorageSection()
  }
  implicit object LinkageSectionDefault   extends Default[LinkageSection]   {
    override def default: LinkageSection = LinkageSection()
  }
  implicit object EnablingSectionDefault  extends Default[EnablingSection]  {
    override def default: EnablingSection = EnablingSection()
  }
  implicit object TimingSectionDefault    extends Default[TimingSection]    {
    override def default: TimingSection = TimingSection()
  }
  implicit object FiringSectionDefault    extends Default[FiringSection]    {
    override def default: FiringSection = FiringSection()
  }

}
