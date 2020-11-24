package io

//import java.awt.Color
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import io.XMLConstantNames._
import model.Position
import model.sim._
//import org.w3c.dom.{Element, NodeList}
import scalafx.scene.paint

import scala.collection.mutable
import scala.language.implicitConversions

object XMLSimRepresentation extends SimRepresentation[xml.Elem] {
  override def represent(x: Sim): xml.Elem         = {
    val timestamp: String            = DateTimeFormatter.ofPattern("E LLL D H:m:s zz u")
      .format(ZonedDateTime.now)
    val userClasses: Array[xml.Elem] = x.classes.map(
      (userClass: UserClass) =>
        <userClass name={userClass.name} priority={
          userClass.priority.toString
        } referenceSource={userClass.referenceSource.name} type={
          userClass.`type`.getClass.getSimpleName
        } />
    ).toArray

    // TODO: This will almost certainly need it's own function once nodes are fully implemented
    val nodes: Array[xml.Elem] =
      x.nodes.map((node: Node) => <node name={node.name}>
        <section className={node.nodeType.getClass.getSimpleName}>
        </section>
      </node>).toArray

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
        <connection source={connection.source.toString} target={
          connection.target.toString
        }/>
    ).toArray

    // TODO: This is a temp hack :))
    <archive xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="TODO" timestamp={
      timestamp
    } xsi:noNamespaceSchemaLocation="Archive.xsd">
      <sim disableStatisticStop="false" logDecimalSeparator="." logDelimiter="," logPath="~/JMT/" logReplaceMode="0" maxEvents="-1" maxSamples="1000000" name="TODO" polling="1.0" xsi:noNamespaceSchemaLocation="SIMmodeldefinition.xsd">
        {userClasses}
        {nodes}
        <measure alpha="0.01" name="Queue 1_Class1_Number of Customers" nodeType="station" precision="0.03" referenceNode="Queue 1" referenceUserClass="Class1" type="Number of Customers" verbose="false"/>
        {connections}
      </sim>
      <jmodel xsi:noNamespaceSchemaLocation="JModelGUI.xsd">
        <userClass color="#FF0000FF" name="Class1"/>
        {nodePositions}
      </jmodel>
      <results>
      </results>
    </archive>
  }

  override def toSim(xmlSim: xml.Elem): Sim        = {
    val xmlSimNodes = xmlSim.child
    val simulation  = xmlSimNodes(1)
    val jmodel      = xmlSimNodes(3)
    //TODO: Handle simulation results
    val results     = xmlSimNodes(5)

    val positionlessNodes: mutable.Map[String, Boolean => Position => Node] =
      mutable.HashMap.empty

    val distributions: mutable.Map[String, Distribution] = mutable.HashMap.empty

    val nodes: mutable.HashMap[String, Node]            = mutable.HashMap.empty
    val connections: mutable.HashSet[Connection]        = mutable.HashSet.empty
    val userClasses: mutable.HashMap[String, UserClass] = mutable.HashMap.empty
    val measures: mutable.HashSet[Measure]              = mutable.HashSet.empty

    val simulationAssets = simulation.head.child

    // Nodes must be made first
    simulationAssets.filter((node: xml.Node) => node.label == "node")
      .foreach((nodeXML: xml.Node) => {
        val (nodeName, node) = nodeFromXML(nodeXML, distributions).get
        positionlessNodes.put(nodeName, node)
      })

    jmodel.child
      .filter((XMLChildren: xml.Node) => XMLChildren.label.equals("station"))
      .foreach(
        (stationXML: xml.Node) =>
          for {
            stationName <- stationXML.attribute("name")
            position    <- stationXML.child(1).headOption
            rotated     <- position.attribute("rotate")
            x           <- position.attribute("x")
            y           <- position.attribute("y")
          } yield nodes.put(
            stationName.toString,
            positionlessNodes(stationName.toString)(rotated.toString.toBoolean)(
              Position(x.toString.toDouble, y.toString.toDouble)
            )
          )
      )

    simulationAssets.foreach((simulationAsset: xml.Node) => {
      simulationAsset.label match {
        case "node" => () // Done already
        case "connection" =>
          connections.add(connectionFromXML(simulationAsset, nodes).get)
        case "userClass" =>
          val (userClassName, userClass) =
            userClassFromXML(simulationAsset, nodes).get
          userClasses.put(userClassName, userClass)
        case "measure" =>
          measures.add(measureFromXML(simulationAsset, nodes, userClasses).get)
        case unimplementedName =>
          if (unimplementedName != "#PCDATA") {
            println(
              "Found unimplemented simulation asset: " + unimplementedName
            )
            println(simulationAsset)
          }
      }
    })

    nodes.values.foreach(println)
    connections.foreach(println)
    measures.foreach(println)
    userClasses.values.foreach(println)
    Sim(
      mutable.HashSet.from(nodes.values),
      connections,
      mutable.HashSet.from(userClasses.values),
      measures,
      mutable.ArrayBuffer.empty
    )
  }

  private def measureFromXML(
      xmlMeasure: xml.Node,
      nodes: collection.Map[String, Node],
      userClasses: collection.Map[String, UserClass]
  ): Option[Measure]                               =
    for {
      classAlpha          <- xmlMeasure.attribute("alpha")
      classReferenceNode  <- xmlMeasure.attribute("referenceNode")
      classReferenceClass <- xmlMeasure.attribute("referenceUserClass")
      classType           <- xmlMeasure.attribute("type")
      classVerbose        <- xmlMeasure.attribute("verbose")
      referenceNode       <- nodes.get(classReferenceNode.toString)
      referenceUserClass  <- userClasses.get(classReferenceClass.toString)
    } yield Measure(
      classAlpha.toString.toFloat,
      referenceNode,
      referenceUserClass,
      classType.toString,
      classVerbose.toString.toBoolean
    )

  private def userClassesFromXML(
      root: Element
  ): mutable.HashMap[String, UserClass]            = {
    val classes: NodeList = root.getElementsByTagName(XML_E_CLASS)

    for (i <- 0 until classes.getLength) {
      val userClass: Element = classes.item(i).asInstanceOf[Element]

      val name: String = userClass.getAttribute(XML_A_CLASS_NAME)

      val str_color: String =
        userClass.getAttribute(XML_A_CLASS_COLOR).stripPrefix("#")

      val color = new Color(Integer.parseInt(str_color, 16), true)

    }
    ???
  }
//  private def userClassFromXML(
//      xmlUserClass: xml.Node,
//      nodes: collection.Map[String, Node]
//  ): Option[(String, UserClass)]                   =
//    for {
//      className            <- xmlUserClass.attribute("name")
//      classPriority        <- xmlUserClass.attribute("priority")
//      classReferenceSource <- xmlUserClass.attribute("referenceSource")
//      classUserClassType   <- xmlUserClass.attribute("type")
//      priority             <- classPriority.headOption
//      referenceSource      <- nodes.get(classReferenceSource.toString)
//    } yield (
//      className.toString,
//      UserClass(
//        className.toString,
//        priority.toString.toInt,
//        referenceSource,
//        if (classUserClassType.toString.equals("open")) UserClass.Open
//        else UserClass.Closed,
//        -1,
//        Exponential(0.5)
//      )
//    )

  private def connectionFromXML(
      xmlConnection: xml.Node,
      nodes: collection.Map[String, Node]
  ): Option[Connection]                            =
    for {
      sourceName <- xmlConnection.attribute("source")
      targetName <- xmlConnection.attribute("target")
      sourceNode <- nodes.get(sourceName.toString)
      targetNode <- nodes.get(targetName.toString)
    } yield Connection(sourceNode, targetNode)

  private def nodeFromXML(
      xmlNode: xml.Node,
      // If it's a source then we check the userclasses
      // to see if it's the refClass. Then we can get
      // the distribution
      distributions: mutable.Map[String, Distribution]
  ): Option[(String, Boolean => Position => Node)] =
    for {
      classNames <- xmlNode.child(1).attribute("className")
      className  <- classNames.headOption
      names      <- xmlNode.attribute("name")
      name       <- names.headOption
    } yield { ??? }

  object Implicit {
    implicit val xmlSimRepresentation: SimRepresentation[xml.Elem] =
      XMLSimRepresentation
  }

  def parseXML(root: Element): Sim                 = {
    var model = Sim.empty

    // Gets optional parameter simulation seed
    val seed = root.getAttribute(XML_A_ROOT_SEED)
    if (seed != null && (seed ne "")) {
      model.useRandomSeed = false
      model.seed = seed.toInt
    }

    // Gets optional parameter maximum time
    val maxTime = root.getAttribute(XML_A_ROOT_DURATION)
    if (maxTime != null && (maxTime ne "")) {
      model.maximumDuration = maxTime.toDouble
    }

    // Gets optional parameter maximum simulated time
    val maxSimulated = root.getAttribute(XML_A_ROOT_SIMULATED)
    if (maxSimulated != null && (maxSimulated ne "")) {
      model.maxSimulatedTime = maxSimulated.toDouble
    }

    // Gets optional parameter polling interval
    val polling = root.getAttribute(XML_A_ROOT_POLLING)
    if (polling != null && (polling ne "")) {
      model.pollingInterval = polling.toDouble
    }

    // Gets optional parameter maximum samples
    val maxSamples = root.getAttribute(XML_A_ROOT_MAXSAMPLES)
    if (maxSamples != null && (maxSamples ne "")) {
      model.maxSamples = maxSamples.toInt
    }

    // Gets optional parameter disable statistic
    val disableStatistic = root.getAttribute(XML_A_ROOT_DISABLESTATISTIC)
    if (disableStatistic != null && (disableStatistic ne "")) {
      model.disableStatistic = disableStatistic.toBoolean
    }

    // Gets optional parameter maximum events
    val maxEvents = root.getAttribute(XML_A_ROOT_MAXEVENTS)
    if (maxEvents != null && (maxEvents ne "")) {
      model.maxEvents = maxEvents.toInt
    }

    // Gets optional parameters log path, replace policy, and delimiter#
    val logPath        = root.getAttribute(XML_A_ROOT_LOGPATH)
    if (logPath != null && (logPath ne "")) {
      val dir = new File(logPath)
      if (dir.isDirectory) model.loggingPath = dir.getAbsolutePath
    }

    val logReplaceMode = root.getAttribute(XML_A_ROOT_LOGREPLACE)
    if (logReplaceMode != null && (logReplaceMode ne "")) {
      model.loggingAutoAppend = logReplaceMode
    }

    val logDelimiter = root.getAttribute(XML_A_ROOT_LOGDELIM)
    if (logDelimiter != null && (logDelimiter ne "")) {
      model.loggingDelim = logDelimiter
    }

    val logDecimalSeparator = root.getAttribute(XML_A_ROOT_LOGDECIMALSEPARATOR)
    if (logDecimalSeparator != null && (logDecimalSeparator ne "")) {
      model.loggingdecimalSeparator = logDecimalSeparator
    }

    parseClasses(root, model)
    empiricalRouting = new HashMap[Array[AnyRef], Map[String, Double]]
    empiricalLDRouting = new HashMap[Array[AnyRef], Map[String, Double]]
    wrrRouting = new HashMap[Array[AnyRef], Map[String, Integer]]
    empiricalFork = new HashMap[Array[AnyRef], Map[String, OutPath]]
    combFork = new HashMap[Array[AnyRef], Map[String, Double]]
    enablingConditionMap = new HashMap[Array[AnyRef], Integer]
    inhibitingConditionMap = new HashMap[Array[AnyRef], Integer]
    firingOutcomeMap = new HashMap[Array[AnyRef], Integer]
    parseStations(root, model)
    parseConnections(root, model)
    parseBlockingRegions(root, model)
    parseMeasures(root, model)
    parsePreloading(root, model)
    // Set reference station for each class
    var keys = refStations.keySet.toArray
    for (key <- keys) {
      val name = refStations.get(key)
      if (
        STATION_TYPE_FORK == name || STATION_TYPE_CLASSSWITCH == name ||
        STATION_TYPE_SCALER == name || STATION_TYPE_TRANSITION == name
      ) model.setClassRefStation(key, name)
      else model.setClassRefStation(key, stations.get(name))
    }
    // Sets correct station key into every empiricalRouting element
    // Now each key is an Object[] where (0) is station key and (1) class key
    keys = empiricalRouting.keySet.toArray
    for (key <- keys) {
      val dualkey = key.asInstanceOf[Array[AnyRef]]
      val rs      = model.getRoutingStrategy(dualkey(0), dualkey(1))
        .asInstanceOf[RoutingStrategy]
      val routing = rs.getValues
      val values  = empiricalRouting.get(key)
      val names   = values.keySet.toArray
      for (name <- names) { routing.put(stations.get(name), values.get(name)) }
    }
    keys = empiricalLDRouting.keySet.toArray
    for (key <- keys) {
      val triplekey = key.asInstanceOf[Array[AnyRef]]
      val ldr       = model.getRoutingStrategy(triplekey(0), triplekey(1))
        .asInstanceOf[LoadDependentRouting]
      val values    = empiricalLDRouting.get(key)
      val names     = values.keySet.toArray
      for (name <- names) {
        ldr.addEmpiricalEntry(
          triplekey(2).asInstanceOf[Integer],
          stations.get(name),
          values.get(name)
        )
      }
    }
    keys = wrrRouting.keySet.toArray
    for (key <- keys) {
      val dualkey = key.asInstanceOf[Array[AnyRef]]
      val rs      = model.getRoutingStrategy(dualkey(0), dualkey(1))
        .asInstanceOf[WeightedRoundRobinRouting]
      val routing = rs.getWeights
      val values  = wrrRouting.get(key)
      val names   = values.keySet.toArray
      for (name <- names) { routing.put(stations.get(name), values.get(name)) }
    }
    keys = empiricalFork.keySet.toArray
    for (key <- keys) {
      val dualkey  = key.asInstanceOf[Array[AnyRef]]
      val fs       =
        model.getForkStrategy(dualkey(0), dualkey(1)).asInstanceOf[ForkStrategy]
      val outPaths = fs.getOutDetails.asInstanceOf[Map[AnyRef, OutPath]]
      val values   = empiricalFork.get(key)
      val names    = values.keySet.toArray
      for (name <- names) { outPaths.put(stations.get(name), values.get(name)) }
    }
    keys = combFork.keySet.toArray
    for (key <- keys) {
      val dualkey = key.asInstanceOf[Array[AnyRef]]
      val fs      =
        model.getForkStrategy(dualkey(0), dualkey(1)).asInstanceOf[ForkStrategy]
      val fork    = fs.getOutDetails.asInstanceOf[Map[AnyRef, Double]]
      val values  = combFork.get(key)
      val names   = values.keySet.toArray
      for (name <- names) { fork.put(name, values.get(name)) }
    }
    keys = enablingConditionMap.keySet.toArray
    for (key <- keys) {
      val quadkey = key.asInstanceOf[Array[AnyRef]]
      val value   = enablingConditionMap.get(key)
      model.setEnablingCondition(
        quadkey(0),
        quadkey(1).asInstanceOf[Integer].intValue,
        stations.get(quadkey(2).asInstanceOf[String]),
        quadkey(3),
        value
      )
    }
    keys = inhibitingConditionMap.keySet.toArray
    for (key <- keys) {
      val quadkey = key.asInstanceOf[Array[AnyRef]]
      val value   = inhibitingConditionMap.get(key)
      model.setInhibitingCondition(
        quadkey(0),
        quadkey(1).asInstanceOf[Integer].intValue,
        stations.get(quadkey(2).asInstanceOf[String]),
        quadkey(3),
        value
      )
    }
    keys = firingOutcomeMap.keySet.toArray
    for (key <- keys) {
      val quadkey = key.asInstanceOf[Array[AnyRef]]
      val value   = firingOutcomeMap.get(key)
      model.setFiringOutcome(
        quadkey(0),
        quadkey(1).asInstanceOf[Integer].intValue,
        stations.get(quadkey(2).asInstanceOf[String]),
        quadkey(3),
        value
      )
    }
  }

  protected def parseClasses(
      root: Element,
      nodes: collection.Map[String, Node]
  ): mutable.HashMap[String, UserClass]            = {
    // Initialize classes and refStations data structure
    val unfinishedClasses
        : mutable.Map[String, (Distribution, paint.Color) => UserClass] =
      mutable.HashMap.empty

    val nodeclasses = root.getElementsByTagName(XML_E_CLASS)

    // Now scans all elements
    for (i <- 0 until nodeclasses.getLength) {
      val currclass  = nodeclasses.item(i).asInstanceOf[Element]
      val name       = currclass.getAttribute(XML_A_CLASS_NAME)
      val `type`     =
        if (currclass.getAttribute(XML_A_CLASS_TYPE) == "open") UserClass.Open
        else UserClass.Closed
      var priority   = 0
      var population = 0

      // As these elements are not mandatory, sets them by default, then tries to parses them
      var tmp = currclass.getAttribute(XML_A_CLASS_CUSTOMERS)
      if (tmp != null && (tmp ne "")) { population = tmp.toInt }
      tmp = currclass.getAttribute(XML_A_CLASS_PRIORITY)
      if (tmp != null && (tmp ne "")) { priority = tmp.toInt }

      // Now adds user class. Note that distribution will be set later.
      val userClass = UserClass(
        name,
        priority,
        referenceSource = nodes(currclass.getAttribute(XML_A_CLASS_REFSOURCE)),
        `type`,
        population,
        _: Distribution,
        _: paint.Color
      )

      unfinishedClasses.put(name, userClass)
    }

    val jmodel = root.getElementsByTagName("jmodel").item(0)
    jmodel.getChildNodes.
  }
}
