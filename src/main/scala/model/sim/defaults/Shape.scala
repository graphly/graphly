package model.sim.defaults

import model.sim._
import util.Default

object Implicit {

  implicit object SinkDefault             extends Default[Sink]             {
    override def default: Sink = Sink(Default[SinkSection])
  }

  implicit object ServerDefault           extends Default[Server]           {
    override def default: Server =
      Server(
        Default[QueueSection],
        UnimplementedSection(<section className="Server">
          <parameter classPath="java.lang.Integer" name="maxJobs">
            <value>1</value>
          </parameter>
          <parameter array="true" classPath="java.lang.Integer" name="numberOfVisits"/>
          <parameter array="true" classPath="jmt.engine.NetStrategies.ServiceStrategy" name="ServiceStrategy"/>
        </section>),
        Default[RouterSection]
      )
  }

  implicit object JoinDefault             extends Default[Join]             {
    override def default: Join =
      Join(UnimplementedSection(<section className="Join">
        <parameter array="true" classPath="jmt.engine.NetStrategies.JoinStrategy" name="JoinStrategy"/>
      </section>), Default[TunnelSection], Default[RouterSection])
  }

  implicit object SourceDefault           extends Default[Source]           {
    override def default: Source =
      Source(
        Default[SourceSection],
        Default[TunnelSection],
        Default[RouterSection]
      )
  }

  implicit object ForkDefault             extends Default[Fork]             {
    override def default: Fork =
      Fork(
        Default[QueueSection],
        Default[TunnelSection],
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
            <parameter array="true" classPath="jmt.engine.NetStrategies.ForkStrategy" name="ForkStrategy"/>
          </section>)
      )
  }

  implicit object SourceSectionDefault    extends Default[SourceSection]    {
    override def default: SourceSection = SourceSection(Seq.empty)
  }
  implicit object TunnelSectionDefault    extends Default[TunnelSection]    {
    override def default: TunnelSection = TunnelSection()
  }
  implicit object RouterSectionDefault    extends Default[RouterSection]    {
    override def default: RouterSection =
      RouterSection(
        <parameter array="true" classPath="jmt.engine.NetStrategies.RoutingStrategy" name="RoutingStrategy"/>
      )
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
        <parameter classPath="java.lang.Integer" name="size">
          <value>-1</value>
        </parameter>
        <parameter array="true" classPath="java.lang.String" name="dropStrategies"/>
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
    override def default: ForkSection = ForkSection()
  }
  implicit object JoinSectionDefault      extends Default[JoinSection]      {
    override def default: JoinSection = JoinSection()
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
