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

  implicit object TerminalDefault extends Default[Terminal] {
    override def default: Terminal = ???
  }

  implicit object RouterDefault extends Default[Router] {
    override def default: Router = Router(
      Default.default(QueueSectionDefault),
      Default.default(TunnelSectionDefault),
      Default.default(RouterSectionDefault)
    )
  }

  implicit object DelayDefault extends Default[Delay] {
    override def default: Delay = ???
  }

  implicit object LoggerDefault extends Default[Logger] {
    override def default: Logger = ???
  }

  implicit object ClassSwitchDefault extends Default[ClassSwitch] {
    override def default: ClassSwitch = ???
  }

  implicit object SemaphoreDefault extends Default[Semaphore] {
    override def default: Semaphore = ???
  }

  implicit object ScalarDefault extends Default[Scalar] {
    override def default: Scalar = ???
  }

  implicit object PlaceDefault extends Default[Place] {
    override def default: Place = ???
  }

  implicit object TransitionDefault extends Default[Transition] {
    override def default: Transition = ???
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
