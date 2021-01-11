package io

import java.io.{ByteArrayInputStream, File}
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import io.XMLConstantNames._
import model.Position
import model.sim._
import scalafx.scene.paint
import scalafx.scene.paint.Color

import scala.collection.mutable
import scala.language.implicitConversions
import scala.math.random
import scala.util.control.Breaks.{break, breakable}

// Please note, this class is completely dependent on JMT and its file format

object XMLSimRepresentation extends SimRepresentation[xml.Elem] {
  def representDistribution(distribution: Distribution): xml.Node =
    distribution match {
      case Uniform(min, max) =>
        <subParameter classPath="jmt.engine.NetStrategies.ServiceStrategies.ServiceTimeStrategy" name="ServiceTimeStrategy">
            <subParameter classPath="jmt.engine.random.Uniform" name="Uniform"/>
          <subParameter classPath="jmt.engine.random.UniformPar" name="distrPar">
            <subParameter classPath="java.lang.Double" name="min">
              <value>{min.toString}</value>
            </subParameter>
            <subParameter classPath="java.lang.Double" name="max">
              <value>{max.toString}</value>
            </subParameter>
          </subParameter>
        </subParameter>
      case Exponential(lambda) =>
        <subParameter classPath="jmt.engine.NetStrategies.ServiceStrategies.ServiceTimeStrategy" name="ServiceTimeStrategy">
          <subParameter classPath="jmt.engine.random.Exponential" name="Exponential"/>
          <subParameter classPath="jmt.engine.random.ExponentialPar" name="distrPar">
            <subParameter classPath="java.lang.Double" name="lambda">
              <value>{lambda.toString}</value>
            </subParameter>
          </subParameter>
        </subParameter>
      case Pareto(alpha, k) => ???
      case Poisson(mean) => ???
      case UnimplementedDistribution(raw) => raw
    }

  def representTypeSection(
      ts: TypeSection,
      classes: mutable.Set[UserClass]
  ): xml.Node                                                              = {
    ts match {
      case SourceSection(refClassNames) =>
        <section className="RandomSource">
          <parameter array="true" classPath="jmt.engine.NetStrategies.ServiceStrategy" name="ServiceStrategy">
            {
          classes.map(userClass => {
            var userClassXml = <refClass>
                {userClass.name}
              </refClass>
            if (refClassNames.contains(userClass.name))
              userClassXml.appended(userClass.distribution.get)

            userClassXml
          })
        }
          </parameter>
        </section>
      case TunnelSection() => <section className="ServiceTunnel"/>
      case RouterSection(routingStrategy) =>
        <section className="Router" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
        {routingStrategy}
      </section>
      case QueueSection(size, dropStrategy, queueingStrategy) =>
        <section className="Queue">
          <parameter classPath="java.lang.Integer" name="size">
            <value>{size.getOrElse(-1).toString}</value>
          </parameter>
          <parameter array="true" classPath="java.lang.String" name="dropStrategies">
            {
          classes.map(userClass => <refClass>
            {userClass.name}
          </refClass>)
        }
            <subParameter classPath="java.lang.String" name="dropStrategy">
              {
          dropStrategy match {
            case Some(strategy) => <value>{strategy.toString}</value>
            case None => ()
          }
        }
            </subParameter>
          </parameter>
        {queueingStrategy.toArray}
      </section>
      case SinkSection() => <section className="JobSink"/>
      case UnimplementedSection(raw) => raw
      case x => {
        println(x.getClass, " isn't implemented!")
        ???
      }
    }
  }

  def representNode(node: Node, classes: mutable.Set[UserClass]): xml.Elem = {
    val sections: Array[xml.Node] = node.nodeType match {
      case Source(sourceSection, tunnelSection, routerSection) =>
        Array(sourceSection, tunnelSection, routerSection)
          .map(representTypeSection(_, classes))
      case Sink(sinkSection) =>
        Array(representTypeSection(sinkSection, classes))
      case Terminal(terminalSection, tunnelSection, routerSection) =>
        Array(terminalSection, tunnelSection, routerSection)
          .map(representTypeSection(_, classes))
      case Router(queueSection, tunnelSection, routerSection) =>
        Array(queueSection, tunnelSection, routerSection)
          .map(representTypeSection(_, classes))
      case Delay(queueSection, delaySection, routerSection) =>
        Array(queueSection, delaySection, routerSection)
          .map(representTypeSection(_, classes))
      case Server(queueSection, serverSection, routerSection) =>
        Array(queueSection, serverSection, routerSection)
          .map(representTypeSection(_, classes))
      case Fork(queueSection, tunnelSection, forkSection) =>
        Array(queueSection, tunnelSection, forkSection)
          .map(representTypeSection(_, classes))
      case Join(joinSection, tunnelSection, routerSection) =>
        Array(joinSection, tunnelSection, routerSection)
          .map(representTypeSection(_, classes))
      case Logger(queueSection, loggerSection, routerSection) =>
        Array(queueSection, loggerSection, routerSection)
          .map(representTypeSection(_, classes))
      case ClassSwitch(queueSection, classSwitch, routerSection) =>
        Array(queueSection, classSwitch, routerSection)
          .map(representTypeSection(_, classes))
      case Semaphore(semaphoreSection, tunnelSection, routerSection) =>
        Array(semaphoreSection, tunnelSection, routerSection)
          .map(representTypeSection(_, classes))
      case Scalar(joinSection, tunnelSection, forkSection) =>
        Array(joinSection, tunnelSection, forkSection)
          .map(representTypeSection(_, classes))
      case Place(storageSection, tunnelSection, linkageSection) =>
        Array(storageSection, tunnelSection, linkageSection)
          .map(representTypeSection(_, classes))
      case Transition(enablingSection, timingSection, firingSection) =>
        Array(enablingSection, timingSection, firingSection)
          .map(representTypeSection(_, classes))
      case Unimplemented(sections) =>
        sections.toArray.map(representTypeSection(_, classes))
    }

    <node name={node.name}>
      {sections}
    </node>
  }

  def representClass(u: UserClass): xml.Elem                               = {
    if (u.referenceSource.isDefined) {
      <userClass name={u.name} priority={u.priority.toString} referenceSource={
        u.referenceSource.get.name
      } type={u.`type`.getClass.getSimpleName.stripSuffix("$")}/>
    } else {
      <userClass name={u.name} priority={u.priority.toString}
                   type={u.`type`.getClass.getSimpleName.stripSuffix("$")}/>
    }
  }

  def representClassGui(u: UserClass): xml.Elem    = {
    val r       = (u.color.red * 255).toInt
    val g       = (u.color.green * 255).toInt
    val b       = (u.color.blue * 255).toInt
    val opacity = (u.color.opacity * 255).toInt

    val color = "#%02X%02X%02X%02X".format(r, g, b, opacity)
    <userClass color={color} name={u.name}/>
  }

  def representMeasure(measure: Measure): xml.Elem =
    <measure alpha={measure.alpha.toString} name={measure.name} nodeType={
      measure.`type`
    } precision={measure.precision.toString} referenceNode={
      measure.referenceNodeName
    } referenceUserClass={measure.referenceClassName} type={
      measure.`type`
    } verbose={measure.verbose.toString} />

  def representBlockingRegion(blockingRegion: BlockingRegion): xml.Node =
    blockingRegion.raw

  override def represent(model: Sim, filename: String): xml.Elem             = {
    val timestamp: String            = DateTimeFormatter.ofPattern("E LLL d H:m:s zz u")
      .format(ZonedDateTime.now)
    val userClasses: Array[xml.Elem] = model.classes.map(representClass).toArray

    val nodes: Array[xml.Elem] =
      model.nodes.map(representNode(_, model.classes)).toArray;

    val nodePositions: Array[xml.Elem] = model.nodes.map(
      (node: Node) =>
        <station name={node.name}>
          <position rotate={node.rotated.toString} x={
          node.position.x.toString
        } y={node.position.y.toString}/>
        </station>
    ).toArray

    val connections: Array[xml.Elem] = model.connections.map(
      (connection: Connection) =>
        <connection source={connection.source.name} target={
          connection.target.name
        }/>
    ).toArray

    val guiClasses = model.classes.map(representClassGui).toArray

    val measures = model.measures.map(representMeasure).toArray

    val blockingRegions =
      model.blockingRegions.map(representBlockingRegion).toArray

    <archive xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name={
      filename
    } timestamp={timestamp} xsi:noNamespaceSchemaLocation="Archive.xsd">
      <sim disableStatisticStop={
      model.configuration.disableStatistic.toString
    } logDecimalSeparator={
      model.configuration.loggingDecimalSeparator
    } logDelimiter={model.configuration.loggingDelim} logPath={
      model.configuration.loggingPath
    } logReplaceMode={model.configuration.loggingAutoAppend} maxEvents={
      model.configuration.maxEvents.toString
    } maxSamples={model.configuration.maxSamples.toString} name={
      filename
    } polling={model.configuration.pollingInterval.toString}
           maxTime={model.configuration.maximumDuration.toString} maxSimulated={
      model.configuration.maxSimulatedTime.toString
    } seed={if (model.configuration.useRandomSeed) (random() * 10e9).toLong.toString else model.configuration.seed.toString} xsi:noNamespaceSchemaLocation="SIMmodeldefinition.xsd">
        {userClasses}{nodes}{measures}{connections}{blockingRegions}
      </sim>
      <jmodel xsi:noNamespaceSchemaLocation="JModelGUI.xsd">
        {guiClasses}{nodePositions}
      </jmodel>
      {
      if (model.results.isDefined) {
        <results>
        {model.results.get}
      </results>
      }
    }
    {
      if (model.traces.nonEmpty)
        model.traces.map(
          trace =>
            <trace x={trace.x.toString} y={trace.y.toString} width={
              trace.width.toString
            } height={trace.height.toString}>
              {
              java.util.Base64.getEncoder
                .encodeToString(trace.image.stream.readAllBytes)
            }
            </trace>
        ).toArray
    }
    </archive>
  }

  override def toSim(xmlSim: xml.Elem): Sim                                  = {
    val xmlSimNodes               = xmlSim.child
    val simulation                = xmlSimNodes(1)
    val results: Option[xml.Node] = xmlSimNodes.find(_.label == XML_E_RESULTS)

    val distributions: mutable.Map[String, Distribution] = mutable.HashMap.empty
    val simulationAssets                                 = simulation.head.child

    // Nodes must be made first
    val nodes = parseNodes(xmlSimNodes, distributions)

    val userClasses: mutable.Map[String, UserClass] =
      parseClasses(xmlSimNodes, nodes, distributions)

    val connections: mutable.Set[Connection] =
      parseConnections(simulationAssets, nodes)

    val measures = parseMeasures(simulationAssets, nodes, userClasses)

    val blockingRegions = parseBlockingRegions(simulationAssets)

    val traces: mutable.Buffer[Trace] = parseTraces(xmlSimNodes)

    val sim = Sim(
      mutable.HashSet.from(nodes.values),
      connections,
      mutable.HashSet.from(userClasses.values),
      measures,
      blockingRegions,
      traces,
      parseConfiguration(xmlSim.child(1)),
      results
    )

    sim
  }

  private def parseTraces(xmlSimNodes: Seq[xml.Node]): mutable.Buffer[Trace] =
    xmlSimNodes.filter(asset => asset.label == XML_E_TRACE)
      .flatMap(traceFromXML).to(mutable.Buffer)

  private def traceFromXML(xmlTrace: xml.Node): Option[Trace] =
    for {
      traceX      <- xmlTrace.attribute(XML_A_TRACE_X)
      traceY      <- xmlTrace.attribute(XML_A_TRACE_Y)
      traceWidth  <- xmlTrace.attribute(XML_A_TRACE_WIDTH)
      traceHeight <- xmlTrace.attribute(XML_A_TRACE_HEIGHT)
    } yield {

      Trace(
        Trace.Image(new ByteArrayInputStream(java.util.Base64.getDecoder.decode(
          xmlTrace.child.head.toString.strip().getBytes
        ))),
        Position(traceX.toString.toDouble, traceY.toString.toDouble),
        traceWidth.toString.toDouble,
        traceHeight.toString.toDouble
      )
    }

  private def parseBlockingRegions(
      simulationAssets: Seq[xml.Node]
  ): mutable.Set[BlockingRegion]                              =
    simulationAssets.filter(asset => asset.label == XML_E_REGION)
      .flatMap(blockingRegionFromXML).to(mutable.Set)

  private def blockingRegionFromXML(
      xmlBlockingRegion: xml.Node
  ): Option[BlockingRegion] =
    for {
      blockingRegionName <- xmlBlockingRegion.attribute(XML_A_REGION_NAME)
    } yield BlockingRegion(blockingRegionName.toString, xmlBlockingRegion)

  private def parseMeasures(
      simulationAssets: Seq[xml.Node],
      nodes: mutable.Map[String, Node],
      userClasses: mutable.Map[String, UserClass]
  ): mutable.Set[Measure]   =
    simulationAssets.filter(asset => asset.label == XML_E_MEASURE)
      .flatMap(measureAsset => measureFromXML(measureAsset, nodes, userClasses))
      .to(mutable.Set)

  private def measureFromXML(
      xmlMeasure: xml.Node,
      nodes: collection.Map[String, Node],
      userClasses: collection.Map[String, UserClass]
  ): Option[Measure]         =
    for {
      measureName              <- xmlMeasure.attribute(XML_A_MEASURE_NAME)
      measureAlpha             <- xmlMeasure.attribute(XML_A_MEASURE_ALPHA)
      measurePrecision         <- xmlMeasure.attribute(XML_A_MEASURE_PRECISION)
      measureVerbose           <- xmlMeasure.attribute(XML_A_MEASURE_VERBOSE)
      measureType              <- xmlMeasure.attribute(XML_A_MEASURE_TYPE)
      measureReferenceClass    <- xmlMeasure.attribute(XML_A_MEASURE_CLASS)
      measureReferenceNode     <- xmlMeasure.attribute(XML_A_MEASURE_STATION)
      measureReferenceNodeType <- xmlMeasure.attribute(XML_A_MEASURE_NODETYPE)
    } yield Measure(
      measureAlpha.toString.toFloat,
      measureName.toString,
      measureReferenceNodeType.toString,
      nodes.get(measureReferenceNode.toString),
      userClasses.get(measureReferenceClass.toString),
      measureType.toString,
      measurePrecision.toString.toFloat,
      measureVerbose.toString.toBoolean
    )

  private def parseConnections(
      simulationAssets: Seq[xml.Node],
      nodes: mutable.Map[String, Node]
  ): mutable.Set[Connection] =
    simulationAssets.filter(asset => asset.label == XML_E_CONNECTION)
      .flatMap(connectionAsset => connectionFromXML(connectionAsset, nodes))
      .to(mutable.Set)

  private def connectionFromXML(
      xmlConnection: xml.Node,
      nodes: collection.Map[String, Node]
  ): Option[Connection]        =
    for {
      sourceName <- xmlConnection.attribute(XML_E_SOURCE)
      targetName <- xmlConnection.attribute(XML_E_TARGET)
      sourceNode <- nodes.get(sourceName.toString)
      targetNode <- nodes.get(targetName.toString)
    } yield Connection(sourceNode, targetNode)

  private def parseNodes(
      xmlSimNodes: Seq[xml.Node],
      distributions: mutable.Map[String, Distribution]
  ): mutable.Map[String, Node] = {

    val simulation                      = xmlSimNodes(1)
    val jmodel                          = xmlSimNodes(3)
    val simulationAssets: Seq[xml.Node] = simulation.head.child

    val positionlessNodes: mutable.Map[String, (Position, Boolean) => Node] =
      mutable.HashMap.empty

    simulationAssets.filter((node: xml.Node) => node.label == XML_E_STATION)
      .foreach((nodeXML: xml.Node) => {
        val (nodeName, node) = nodeFromXML(nodeXML, distributions).get
        positionlessNodes.put(nodeName, node)
      })

    // Finish the nodes
    val nodes: mutable.HashMap[String, Node] = mutable.HashMap.empty

    jmodel.child
      .filter((XMLChildren: xml.Node) => XMLChildren.label == NODETYPE_STATION)
      .foreach(
        (stationXML: xml.Node) =>
          for {
            stationName <- stationXML.attribute(XML_A_STATION_NAME)
            position    <- stationXML.child(1).headOption
            rotated     <- position.attribute(XML_A_POSITION_ROTATE)
            x           <- position.attribute(XML_A_POSITION_X)
            y           <- position.attribute(XML_A_POSITION_Y)
          } yield nodes.put(
            stationName.toString,
            positionlessNodes(stationName.toString)(
              Position(x.toString.toDouble, y.toString.toDouble),
              rotated.toString.toBoolean
            )
          )
      )

    nodes
  }

  private def nodeFromXML(
      xmlNode: xml.Node,
      distributions: mutable.Map[String, Distribution]
  ): Option[(String, (Position, Boolean) => Node)]      =
    for {
      names <- xmlNode.attribute(XML_A_STATION_NAME)
      name  <- names.headOption
    } yield (
      name.toString,
      Node(
        name.toString,
        _: Position,
        makeNodeType(xmlNode, distributions),
        _: Boolean
      )
    )

  def parseConfiguration(root: xml.Node): Configuration = {

    val configuration = Configuration()
    // Gets optional parameter simulation seed
    val seed          = root.attribute(XML_A_ROOT_SEED)
    if (seed.isDefined) {
      configuration.useRandomSeed = false
      configuration.seed = seed.get.head.toString.toInt
    }

    // Gets optional parameter maximum time
    val maxTime = root.attribute(XML_A_ROOT_DURATION)
    if (maxTime.isDefined) {
      configuration.maximumDuration = maxTime.get.head.toString.toDouble
    }

    // Gets optional parameter maximum simulated time
    val maxSimulated = root.attribute(XML_A_ROOT_SIMULATED)
    if (maxSimulated.isDefined) {
      configuration.maxSimulatedTime = maxSimulated.get.head.toString.toDouble
    }

    // Gets optional parameter polling interval
    val polling = root.attribute(XML_A_ROOT_POLLING)
    if (polling.isDefined) {
      configuration.pollingInterval = polling.get.head.toString.toDouble
    }

    // Gets optional parameter maximum samples
    val maxSamples = root.attribute(XML_A_ROOT_MAXSAMPLES)
    if (maxSamples.isDefined) {
      configuration.maxSamples = maxSamples.get.head.toString.toInt
    }

    // Gets optional parameter disable statistic
    val disableStatistic = root.attribute(XML_A_ROOT_DISABLESTATISTIC)
    if (disableStatistic.isDefined) {
      configuration.disableStatistic =
        disableStatistic.get.head.toString.toBoolean
    }

    // Gets optional parameter maximum events
    val maxEvents = root.attribute(XML_A_ROOT_MAXEVENTS)
    if (maxEvents.isDefined) {
      configuration.maxEvents = maxEvents.get.head.toString.toInt
    }

    // Gets optional parameters log path, replace policy, and delimiter
    val logPath        = root.attribute(XML_A_ROOT_LOGPATH)
    if (logPath.isDefined) {
      val dir = new File(logPath.toString)
      if (dir.isDirectory) configuration.loggingPath = dir.getAbsolutePath
    }

    val logReplaceMode = root.attribute(XML_A_ROOT_LOGREPLACE)
    if (logReplaceMode.isDefined) {
      configuration.loggingAutoAppend = logReplaceMode.get.head.toString
    }

    val logDelimiter = root.attribute(XML_A_ROOT_LOGDELIM)
    if (logDelimiter.isDefined) {
      configuration.loggingDelim = logDelimiter.get.head.toString
    }

    val logDecimalSeparator = root.attribute(XML_A_ROOT_LOGDECIMALSEPARATOR)
    if (logDecimalSeparator.isDefined) {
      configuration.loggingDecimalSeparator =
        logDecimalSeparator.get.head.toString
    }

    configuration
  }

  protected def parseClasses(
      root: Seq[xml.Node],
      nodes: collection.Map[String, Node],
      distributions: mutable.Map[String, Distribution]
  ): mutable.Map[String, UserClass] = {
    // Initialize classes and refStations data structure
    val unfinishedClasses: mutable.Map[
      String,
      (Option[Distribution], paint.Color) => UserClass
    ] = mutable.HashMap.empty

    val finishedClasses: mutable.Map[String, UserClass] = mutable.HashMap.empty

    val nodeclasses = root.find(node => node.label == "sim").get.child
      .filter(node => node.label == XML_E_CLASS)

    // Now scans all elements
    for (node <- nodeclasses) {
      val currClass  = node.asInstanceOf[xml.Elem]
      val name       = currClass.attribute(XML_A_CLASS_NAME).get.head.toString
      val `type`     =
        if (currClass.attribute(XML_A_CLASS_TYPE).get.head.toString == "open")
          UserClass.Open
        else UserClass.Closed
      var priority   = 0
      var population = 0

      // As these elements are not mandatory, sets them by default, then tries to parses them
      var tmp = currClass.attribute(XML_A_CLASS_CUSTOMERS)
      if (tmp.isDefined) population = tmp.get.head.toString.toInt
      tmp = currClass.attribute(XML_A_CLASS_PRIORITY)
      if (tmp.isDefined) priority = tmp.get.head.toString.toInt

      // Now create the userClass. Note that distribution will be set later.
      val userClass = UserClass(
        name,
        priority,
        referenceSource = currClass.attribute(XML_A_CLASS_REFSOURCE) match {
          case Some(nodeName) => nodes.get(nodeName.head.toString)
          case None => None
        },
        `type`,
        population,
        _: Option[Distribution],
        _: paint.Color
      )

      unfinishedClasses.put(name, userClass)
    }

    val jmodel = root.find(node => node.label == "jmodel").get
    jmodel.child.filter(node => node.label == XML_E_CLASS)
      .foreach(classData => {
        val colorString: String = classData.attribute(XML_A_CLASS_COLOR).get
          .head.toString.stripPrefix("#")

        val alpha = Integer.parseInt(colorString.substring(6, 8), 16)
        val color = Color.web(colorString, alpha / 255)

        val classToComplete =
          classData.attribute(XML_A_CLASS_NAME).get.head.toString
        val finishedClass   = unfinishedClasses(classToComplete)(
          // This is when there's a user class, but no nodes
          distributions.get(classToComplete),
          color
        )

        finishedClasses.put(classToComplete, finishedClass)
      })

    finishedClasses
  }

  private def makeNodeType(
      station: xml.Node,
      distributions: mutable.Map[String, Distribution]
  ): NodeType                       = {
    val sections: Seq[xml.Node] =
      station.child.filter(node => node.label == XML_E_STATION_SECTION)
    val sectionNames            = sections
      .flatMap(section => section.attribute(XML_A_STATION_SECTION_CLASSNAME))
      .map(attribute => attribute.toString)

    // Finds station type, basing on section names
    if (
      sectionNames(0) == CLASSNAME_SOURCE &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_ROUTER
    ) {
      Source(
        makeSourceSection(sections(0), distributions),
        makeTunnelSection(),
        makeRouterSection(sections(2))
      )
    } else if (sectionNames(0) == CLASSNAME_SINK) Sink(makeSinkSection())
    else if (
      sectionNames(0) == CLASSNAME_TERMINAL &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_ROUTER
    )
      Terminal(
        makeUnimplementedSection(sections(0)),
        makeTunnelSection(),
        makeRouterSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_ROUTER
    )
      Router(
        makeQueueSection(sections(0)),
        makeTunnelSection(),
        makeRouterSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_DELAY && sectionNames(2) == CLASSNAME_ROUTER
    )
      Delay(
        makeQueueSection(sections(0)),
        makeUnimplementedSection(sections(1)),
        makeRouterSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      (sectionNames(1) == CLASSNAME_SERVER ||
      // PSSERVER seems to be a priority flag
      sectionNames(1) == CLASSNAME_PSSERVER) &&
      sectionNames(2) == CLASSNAME_ROUTER
    )
      Server(
        makeQueueSection(sections(0)),
        makeUnimplementedSection(sections(1)),
        makeRouterSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_FORK
    )
      Fork(
        makeQueueSection(sections(0)),
        makeTunnelSection(),
        makeUnimplementedSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_JOIN &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_ROUTER
    )
      Join(
        makeUnimplementedSection(sections(0)),
        makeTunnelSection(),
        makeRouterSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_LOGGER && sectionNames(2) == CLASSNAME_ROUTER
    )
      Logger(
        makeQueueSection(sections(0)),
        makeUnimplementedSection(sections(1)),
        makeRouterSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_CLASSSWITCH &&
      sectionNames(2) == CLASSNAME_ROUTER
    )
      ClassSwitch(
        makeQueueSection(sections(0)),
        makeUnimplementedSection(sections(1)),
        makeRouterSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_SEMAPHORE &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_ROUTER
    )
      Semaphore(
        makeUnimplementedSection(sections(0)),
        makeTunnelSection(),
        makeRouterSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_JOIN &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_FORK
    )
      Scalar(
        makeUnimplementedSection(sections(0)),
        makeTunnelSection(),
        makeUnimplementedSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_STORAGE &&
      sectionNames(1) == CLASSNAME_TUNNEL &&
      sectionNames(2) == CLASSNAME_LINKAGE
    )
      Place(
        makeUnimplementedSection(sections(0)),
        makeTunnelSection(),
        makeUnimplementedSection(sections(2))
      )
    else if (
      sectionNames(0) == CLASSNAME_ENABLING &&
      sectionNames(1) == CLASSNAME_TIMING && sectionNames(2) == CLASSNAME_FIRING
    )
      Transition(
        makeUnimplementedSection(sections(0)),
        makeUnimplementedSection(sections(1)),
        makeUnimplementedSection(sections(2))
      )
    else
      Unimplemented(sections.map(section => makeUnimplementedSection(section)))
  }

  private def makeUnimplementedSection[T <: TypeSection](
      sectionXml: xml.Node
  ): UnimplementedSection[T]        = UnimplementedSection(sectionXml)

  private def makeSourceSection(
      sectionXml: xml.Node,
      distributions: mutable.Map[String, Distribution]
  ): SourceSection                                                             = {
    val refClassNames: mutable.Buffer[String] = mutable.Buffer.empty

    val strategiesParent = sectionXml.child(1).child

    if (strategiesParent.nonEmpty) {
      val strategies = strategiesParent.tail.filterNot(x => x.toString == "\n")
      for (i <- strategies.indices.filter(n => n % 2 == 0)) {
        breakable {
          val refClassName: String = strategies(i).child.head.toString()
          // If the class is closed, then this will just contain "null" & isn't relevant
          val distributionXml      = strategies(i + 1)
          if (distributionXml.head.toString() == "null") break()

          val distribution: Distribution =
            UnimplementedDistribution(distributionXml)
          distributions.put(refClassName, distribution)
          refClassNames.addOne(refClassName)
        }
      }
    }

    SourceSection(refClassNames.toSeq)
  }
  private def makeTunnelSection(): TunnelSection                               = TunnelSection()
  private def makeRouterSection(sectionXml: xml.Node): RouterSection           =
    RouterSection(sectionXml.child(1))
  private def makeSinkSection(): SinkSection                                   = SinkSection()
  private def makeTerminalSection(sectionXml: xml.Node): TerminalSection       = ???
  private def makeQueueSection(sectionXml: xml.Node): QueueSection             = {
    val parsedSize         = sectionXml.child(1).child(1).child(0).toString.toInt
    val size               = if (parsedSize < -1) None else Some(parsedSize)
    val parsedDropStrategy =
      sectionXml.child(3).child.find(node => node.label == XML_E_SUBPARAMETER)

    val dropStrategy = parsedDropStrategy match {
      case Some(dropStrategyXml) =>
        Some(DropStrategy.withName(dropStrategyXml.child(1).child(0).toString))
      case None => None
    }

    QueueSection(size, dropStrategy, sectionXml.child.drop(4))
  }

  private def makeDelaySection(sectionXml: xml.Node): DelaySection             = ???
  private def makeServerSection(sectionXml: xml.Node): ServerSection           = ???
  private def makeForkSection(sectionXml: xml.Node): ForkSection               = ???
  private def makeJoinSection(sectionXml: xml.Node): JoinSection               = ???
  private def makeLoggerSection(sectionXml: xml.Node): LoggerSection           = ???
  private def makeClassSwitchSection(sectionXml: xml.Node): ClassSwitchSection =
    ???
  private def makeSemaphoreSection(sectionXml: xml.Node): SemaphoreSection     = ???
  private def makeStorageSection(sectionXml: xml.Node): StorageSection         = ???
  private def makeLinkageSection(sectionXml: xml.Node): LinkageSection         = ???
  private def makeEnablingSection(sectionXml: xml.Node): EnablingSection       = ???
  private def makeTimingSection(sectionXml: xml.Node): TimingSection           = ???
  private def makeFiringSection(sectionXml: xml.Node): FiringSection           = ???

  object Implicit {
    implicit val xmlSimRepresentation: SimRepresentation[xml.Elem] =
      XMLSimRepresentation
  }
}
