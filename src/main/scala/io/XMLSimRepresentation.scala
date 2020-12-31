package io

import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import io.XMLConstantNames._
import model.Position
import model.sim._
import scalafx.scene.paint
import scalafx.scene.paint.Color

import scala.collection.mutable
import scala.language.implicitConversions
import scala.util.control.Breaks.{break, breakable}

// Please note, this class is completely dependent on JMT and its file format

object XMLSimRepresentation extends SimRepresentation[xml.Elem] {
  def representNode(node: Node): xml.Elem    = {
    val sections: Array[xml.Node] = node.nodeType match {
      case Source(sourceSection, tunnelSection, routerSection) => ???
//        Array(sourceSection.raw, tunnelSection.raw, routerSection.raw)
      case Sink(sinkSection) => ??? //Array(sinkSection.raw)
      case Terminal(terminalSection, tunnelSection, routerSection) => ???
//        Array(terminalSection.raw, tunnelSection.raw, routerSection.raw)
      case Router(queueSection, tunnelSection, routerSection) => ???
//        Array(queueSection.raw, tunnelSection.raw, routerSection.raw)
      case Delay(queueSection, delaySection, routerSection) => ???
//        Array(queueSection.raw, delaySection.raw, routerSection.raw)
      case Server(queueSection, serverSection, routerSection) => ???
//        Array(queueSection.raw, serverSection.raw, routerSection.raw)
      case Fork(queueSection, tunnelSection, forkSection) => ???
//        Array(queueSection.raw, tunnelSection.raw, forkSection.raw)
      case Join(joinSection, tunnelSection, routerSection) => ???
//        Array(joinSection.raw, tunnelSection.raw, routerSection.raw)
      case Logger(queueSection, loggerSection, routerSection) => ???
//        Array(queueSection.raw, loggerSection.raw, routerSection.raw)
      case ClassSwitch(queueSection, classSwitch, routerSection) => ???
//        Array(queueSection.raw, classSwitch.raw, routerSection.raw)
      case Semaphore(semaphoreSection, tunnelSection, routerSection) => ???
//        Array(semaphoreSection.raw, tunnelSection.raw, routerSection.raw)
      case Scalar(joinSection, tunnelSection, forkSection) => ???
//        Array(joinSection.raw, tunnelSection.raw, forkSection.raw)
      case Place(storageSection, tunnelSection, linkageSection) => ???
//        Array(storageSection.raw, tunnelSection.raw, linkageSection.raw)
      case Transition(enablingSection, timingSection, firingSection) => ???
//        Array(enablingSection.raw, timingSection.raw, firingSection.raw)
      case Unimplemented(sections) => sections.map(x => x.raw).toArray
    }

    <node name={node.name}>
      {sections}
    </node>
  }

  def representClass(u: UserClass): xml.Elem = {
    if (u.referenceSource.isDefined) {
      <userClass name={u.name} priority={u.priority.toString} referenceSource={
        u.referenceSource.get.name
      } type={u.`type`.getClass.getSimpleName}/>
    } else {
      <userClass name={u.name} priority={u.priority.toString} 
                   type={u.`type`.getClass.getSimpleName}/>
    }
  }

  def representClassGui(u: UserClass): xml.Elem              = {
    val r       = (u.color.red * 255).toInt
    val g       = (u.color.green * 255).toInt
    val b       = (u.color.blue * 255).toInt
    val opacity = (u.color.opacity * 255).toInt

    val color = "#%02X%02X%02X%02X".format(r, g, b, opacity)
    println(color)
    <userClass color={color} name={u.name}/>
  }

  override def represent(x: Sim, filename: String): xml.Elem = {
    val timestamp: String            = DateTimeFormatter.ofPattern("E LLL d H:m:s zz u")
      .format(ZonedDateTime.now)
    val userClasses: Array[xml.Elem] = x.classes.map(representClass).toArray

    // TODO delete this commented-out code before merging
    //
    //    val nodes: Array[xml.Elem] =
    //      x.nodes.map((node: Node) => <node name={node.name}>
    //        <section className={node.nodeType.getClass.getSimpleName}>
    //        </section>
    //      </node>).toArray
    val nodes: Array[xml.Elem] = x.nodes.map(representNode).toArray;

    val nodePositions: Array[xml.Elem] = x.nodes.map(
      (node: Node) =>
        <station name={node.name}>
          <position rotate="false" x={node.position.x.toString} y={
          node.position.y.toString
        }/>
        </station>
    ).toArray

    val connections: Array[xml.Elem] = x.connections.map(
      (connection: Connection) =>
        <connection source={connection.source.name} target={
          connection.target.name
        }/>
    ).toArray

    // TODO: This is a temp hack :))

    //TODO: Scale by 1/255 ??
    //    val colorr = x.classes.head.color

    //    val color = "#%02X%02X%02X%02X".format(colorr.red, colorr.green, colorr.blue, (colorr.opacity * 255).toInt);
    //    println(color)

    val guiClasses = x.classes.map(representClassGui).toArray

    // <measure alpha="0.01" name="Queue 1_Class1_Number of Customers" nodeType="station" precision="0.03" referenceNode="Queue 1" referenceUserClass="Class1" type="Number of Customers" verbose="false"/>
    <archive xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name={
      filename
    } timestamp={timestamp} xsi:noNamespaceSchemaLocation="Archive.xsd">
      <sim disableStatisticStop={
      x.disableStatistic.toString
    } logDecimalSeparator={x.loggingDecimalSeparator} logDelimiter={
      x.loggingDelim
    } logPath={x.loggingPath} logReplaceMode={x.loggingAutoAppend} maxEvents={
      x.maxEvents.toString
    } maxSamples={x.maxSamples.toString} name={filename} polling={
      x.pollingInterval.toString
    } xsi:noNamespaceSchemaLocation="SIMmodeldefinition.xsd">
        {userClasses}{nodes}{connections}
      </sim>
      <jmodel xsi:noNamespaceSchemaLocation="JModelGUI.xsd">
        {guiClasses}{nodePositions}
      </jmodel>
      <results>
      </results>
    </archive>
  }

  override def toSim(xmlSim: xml.Elem): Sim                  = {
    val xmlSimNodes               = xmlSim.child
    val simulation                = xmlSimNodes(1)
    //TODO: Handle simulation results
    var results: Option[xml.Node] = None
    if (xmlSimNodes.length > 5) { results = Some(xmlSimNodes(5)) }

    val distributions: mutable.Map[String, Distribution] = mutable.HashMap.empty
    val simulationAssets                                 = simulation.head.child

    // Nodes must be made first
    val nodes = parseNodes(xmlSimNodes, distributions)

    val userClasses: mutable.Map[String, UserClass] =
      parseClasses(xmlSimNodes, nodes, distributions)

    val connections: mutable.Set[Connection] =
      parseConnections(simulationAssets, nodes)

    val measures = parseMeasures(simulationAssets, nodes, userClasses)

    val unconfiguredSim = Sim(
      mutable.HashSet.from(nodes.values),
      connections,
      mutable.HashSet.from(userClasses.values),
      measures,
      mutable.ArrayBuffer.empty,
      results
    )

    val sim = parseSim(xmlSim, unconfiguredSim)

    nodes.values.foreach(println)
    connections.foreach(println)
    measures.foreach(println)
    userClasses.values.foreach(println)

    sim
  }

  private def parseMeasures(
      simulationAssets: Seq[xml.Node],
      nodes: mutable.Map[String, Node],
      userClasses: mutable.Map[String, UserClass]
  ): mutable.Set[Measure]                                    =
    simulationAssets.filter(asset => asset.label == XML_E_MEASURE)
      .flatMap(measureAsset => measureFromXML(measureAsset, nodes, userClasses))
      .to(mutable.Set)

  private def measureFromXML(
      xmlMeasure: xml.Node,
      nodes: collection.Map[String, Node],
      userClasses: collection.Map[String, UserClass]
  ): Option[Measure]         =
    for {
      classAlpha          <- xmlMeasure.attribute(XML_A_MEASURE_ALPHA)
      classReferenceNode  <- xmlMeasure.attribute(XML_A_MEASURE_STATION)
      classReferenceClass <- xmlMeasure.attribute(XML_A_MEASURE_CLASS)
      classType           <- xmlMeasure.attribute(XML_A_MEASURE_TYPE)
      classVerbose        <- xmlMeasure.attribute(XML_A_MEASURE_VERBOSE)
      referenceNode       <- nodes.get(classReferenceNode.toString)
      referenceUserClass  <- userClasses.get(classReferenceClass.toString)
    } yield Measure(
      classAlpha.toString.toFloat,
      referenceNode,
      referenceUserClass,
      classType.toString,
      classVerbose.toString.toBoolean
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
  ): Option[(String, (Position, Boolean) => Node)] =
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

  def parseSim(root: xml.Elem, model: Sim): Sim    = {

    // Gets optional parameter simulation seed
    val seed = root.attribute(XML_A_ROOT_SEED)
    if (seed.isDefined) {
      model.useRandomSeed = false
      model.seed = seed.get.head.toString.toInt
    }

    // Gets optional parameter maximum time
    val maxTime = root.attribute(XML_A_ROOT_DURATION)
    if (maxTime.isDefined) {
      model.maximumDuration = maxTime.get.head.toString.toDouble
    }

    // Gets optional parameter maximum simulated time
    val maxSimulated = root.attribute(XML_A_ROOT_SIMULATED)
    if (maxSimulated.isDefined) {
      model.maxSimulatedTime = maxSimulated.get.head.toString.toDouble
    }

    // Gets optional parameter polling interval
    val polling = root.attribute(XML_A_ROOT_POLLING)
    if (polling.isDefined) {
      model.pollingInterval = polling.get.head.toString.toDouble
    }

    // Gets optional parameter maximum samples
    val maxSamples = root.attribute(XML_A_ROOT_MAXSAMPLES)
    if (maxSamples.isDefined) {
      model.maxSamples = maxSamples.get.head.toString.toInt
    }

    // Gets optional parameter disable statistic
    val disableStatistic = root.attribute(XML_A_ROOT_DISABLESTATISTIC)
    if (disableStatistic.isDefined) {
      model.disableStatistic = disableStatistic.get.head.toString.toBoolean
    }

    // Gets optional parameter maximum events
    val maxEvents = root.attribute(XML_A_ROOT_MAXEVENTS)
    if (maxEvents.isDefined) {
      model.maxEvents = maxEvents.get.head.toString.toInt
    }

    // Gets optional parameters log path, replace policy, and delimiter#
    val logPath        = root.attribute(XML_A_ROOT_LOGPATH)
    if (logPath.isDefined) {
      val dir = new File(logPath.toString)
      if (dir.isDirectory) model.loggingPath = dir.getAbsolutePath
    }

    val logReplaceMode = root.attribute(XML_A_ROOT_LOGREPLACE)
    if (logReplaceMode.isDefined) {
      model.loggingAutoAppend = logReplaceMode.get.head.toString
    }

    val logDelimiter = root.attribute(XML_A_ROOT_LOGDELIM)
    if (logDelimiter.isDefined) {
      model.loggingDelim = logDelimiter.get.head.toString
    }

    val logDecimalSeparator = root.attribute(XML_A_ROOT_LOGDECIMALSEPARATOR)
    if (logDecimalSeparator.isDefined) {
      model.loggingDecimalSeparator = logDecimalSeparator.get.head.toString
    }

    model
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
        referenceSource = nodes
          .get(currClass.attribute(XML_A_CLASS_REFSOURCE).get.head.toString),
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
//      Terminal(makeTerminalSection(sections(0)), makeTunnelSection(sections(1)), makeRouterSection(sections(2)))
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_ROUTER
    )
      Router(
        makeQueueSection(sections(0)),
        makeTunnelSection(),
        makeRouterSection(sections(2))
      )
//    Router(makeQueueSection(sections(0)), makeTunnelSection(sections(1)), makeRouterSection(sections(2)))
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_DELAY && sectionNames(2) == CLASSNAME_ROUTER
    )
      Delay(
        makeQueueSection(sections(0)),
        makeUnimplementedSection(sections(1)),
        makeRouterSection(sections(2))
      )
//    Delay(makeQueueSection(sections(0)), makeDelaySection(sections(1)), makeRouterSection(sections(2)))
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
//    Server(makeQueueSection(sections(0)), makeServerSection(sections(1)), makeRouterSection(sections(2)))
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_FORK
    )
      Fork(
        makeQueueSection(sections(0)),
        makeTunnelSection(),
        makeUnimplementedSection(sections(2))
      )
//      Fork(makeQueueSection(sections(0)), makeTunnelSection(sections(1)), makeForkSection(sections(2)))
    else if (
      sectionNames(0) == CLASSNAME_JOIN &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_ROUTER
    )
      Join(
        makeUnimplementedSection(sections(0)),
        makeTunnelSection(),
        makeRouterSection(sections(2))
      )
//    Join(makeJoinSection(sections(0)), makeTunnelSection(sections(1)), makeRouterSection(sections(2)))
    else if (
      sectionNames(0) == CLASSNAME_QUEUE &&
      sectionNames(1) == CLASSNAME_LOGGER && sectionNames(2) == CLASSNAME_ROUTER
    )
      Logger(
        makeQueueSection(sections(0)),
        makeUnimplementedSection(sections(1)),
        makeRouterSection(sections(2))
      )
//      Logger(makeQueueSection(sections(0)), makeLoggerSection(sections(1)), makeRouterSection(sections(2)))
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
//     ClassSwitch(makeQueueSection(sections(0)), makeClassSwitchSection(sections(1)), makeRouterSection(sections(2)))
    else if (
      sectionNames(0) == CLASSNAME_SEMAPHORE &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_ROUTER
    )
      Semaphore(
        makeUnimplementedSection(sections(0)),
        makeTunnelSection(),
        makeRouterSection(sections(2))
      )
//    Semaphore(makeSemaphoreSection(sections(0)), makeTunnelSection(sections(1)), makeRouterSection(sections(2)))
    else if (
      sectionNames(0) == CLASSNAME_JOIN &&
      sectionNames(1) == CLASSNAME_TUNNEL && sectionNames(2) == CLASSNAME_FORK
    )
      Scalar(
        makeUnimplementedSection(sections(0)),
        makeTunnelSection(),
        makeUnimplementedSection(sections(2))
      )
//    Scalar(makeJoinSection(sections(0)), makeTunnelSection(sections(1)), makeForkSection(sections(2)))
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
//    Place(makeStorageSection(sections(0)), makeTunnelSection(sections(1)), makeLinkageSection(sections(2)))
    else if (
      sectionNames(0) == CLASSNAME_ENABLING &&
      sectionNames(1) == CLASSNAME_TIMING && sectionNames(2) == CLASSNAME_FIRING
    )
      Transition(
        makeUnimplementedSection(sections(0)),
        makeUnimplementedSection(sections(1)),
        makeUnimplementedSection(sections(2))
      )
//      Transition(makeEnablingSection(sections(0)), makeTimingSection(sections(1)), makeFiringSection(sections(2)))

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

    val strategies =
      sectionXml.child(1).child.tail.filterNot(x => x.toString == "\n")
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

    SourceSection(refClassNames.toSeq)
  }
  private def makeTunnelSection(): TunnelSection                               = TunnelSection()
  private def makeRouterSection(sectionXml: xml.Node): RouterSection           =
    RouterSection(sectionXml)
  private def makeSinkSection(): SinkSection                                   = SinkSection()
  private def makeTerminalSection(sectionXml: xml.Node): TerminalSection       = ???
  private def makeQueueSection(sectionXml: xml.Node): QueueSection             =
    QueueSection(sectionXml)
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
