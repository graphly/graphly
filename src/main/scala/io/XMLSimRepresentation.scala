package io

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import model.Position
import model.sim.Shape.Metadata
import model.sim._

import scala.collection.mutable
import scala.language.implicitConversions

object XMLSimRepresentation extends SimRepresentation[xml.Elem] {
  override def represent(x: Sim): xml.Elem                       = {
    val timestamp: String      = DateTimeFormatter.ofPattern("E LLL d H:m:s zz u")
      .format(ZonedDateTime.now)
    // TODO: This will almost certainly need it's own function once nodes are fully implemented
    val nodes: Array[xml.Elem] =
      x.nodes.map((node: Node) => <node name={node.name} ></node>).toArray

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
        <userClass name="Class1" priority="0" referenceSource="Source 1" type="open"/>
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

  override def toSim(xmlSim: xml.Elem): Sim                      = {
    val xmlSimNodes = xmlSim.child
    val simulation  = xmlSimNodes(1)
    val jmodel      = xmlSimNodes(3)
    //TODO: Handle simulation results
    val results     = xmlSimNodes(5)

    val positionlessNodes: mutable.HashMap[String, Position => Node] =
      mutable.HashMap.empty

    val nodes: mutable.HashMap[String, Node]            = mutable.HashMap.empty
    val connections: mutable.HashSet[Connection]        = mutable.HashSet.empty
    val userClasses: mutable.HashMap[String, UserClass] = mutable.HashMap.empty
    val measures: mutable.HashSet[Measure]              = mutable.HashSet.empty

    val simulationAssets = simulation.head.child

    // Nodes must be made first
    simulationAssets.filter((node: xml.Node) => node.label == "node")
      .foreach((nodeXML: xml.Node) => {
        val (nodeName, node) = nodeFromXML(nodeXML).get
        positionlessNodes.put(nodeName, node)
      })

    jmodel.child
      .filter((XMLChildren: xml.Node) => XMLChildren.label.equals("station"))
      .foreach(
        (stationXML: xml.Node) =>
          for {
            stationName <- stationXML.attribute("name")
            position    <- stationXML.child(1).headOption
            x           <- position.attribute("x")
            y           <- position.attribute("y")
          } yield nodes.put(
            stationName.toString,
            positionlessNodes(stationName.toString)(
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
  ): Option[Measure]                                             =
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

  private def userClassFromXML(
      xmlUserClass: xml.Node,
      nodes: collection.Map[String, Node]
  ): Option[(String, UserClass)]                                 =
    for {
      className            <- xmlUserClass.attribute("name")
      classPriority        <- xmlUserClass.attribute("priority")
      classReferenceSource <- xmlUserClass.attribute("referenceSource")
      classUserClassType   <- xmlUserClass.attribute("type")
      priority             <- classPriority.headOption
      referenceSource      <- nodes.get(classReferenceSource.toString)
    } yield (
      className.toString,
      UserClass(
        className.toString,
        priority.toString.toInt,
        referenceSource,
        if (classUserClassType.toString.equals("open")) UserClass.Open
        else UserClass.Closed
      )
    )

  private def connectionFromXML(
      xmlConnection: xml.Node,
      nodes: collection.Map[String, Node]
  ): Option[Connection]                                          =
    for {
      sourceName <- xmlConnection.attribute("source")
      targetName <- xmlConnection.attribute("target")
      sourceNode <- nodes.get(sourceName.toString)
      targetNode <- nodes.get(targetName.toString)
    } yield Connection(sourceNode, targetNode)

  private def nodeMetadataFromXML(xmlParams: xml.Node): Metadata = {
    val params: mutable.HashMap[String, String] = mutable.HashMap.empty
    for {
      parameters     <- xmlParams.child.drop(1)
      parameterNames <- parameters.attribute("name")
    } yield parameters.zip(parameterNames).foreach {
      case (paramNode, paramNameNode) =>
        val paramName        = paramNameNode.toString
        val paramVal: String = paramName match {
          case "size" => paramNode.child(1).text
          case "FCFSstrategy" => "FCFSstrategy"
          case _ => ""
        }
        params.put(paramName, paramVal)
    }

    params
  }

  private def nodeFromXML(
      xmlNode: xml.Node
  ): Option[(String, Position => Node)]                          =
    for {
      classNames <- xmlNode.child(1).attribute("className")
      className  <- classNames.headOption
      names      <- xmlNode.attribute("name")
      name       <- names.headOption
    } yield {
      className.toString match {
        //TODO: Add other node types
        //TODO: Nodes appear to have multiple "sections",
        // which can be handled in specific constructors
        case "Queue" =>
          (
            name.toString,
            Queue(
              nodeMetadataFromXML(xmlNode.child(1)),
              name.toString,
              _: Position
            )
          )
        case "RandomSource" =>
          (
            name.toString,
            Source(
              nodeMetadataFromXML(xmlNode.child(1)),
              name.toString,
              _: Position
            )
          )
        case "JobSink" =>
          (
            name.toString,
            Sink(
              nodeMetadataFromXML(xmlNode.child(1)),
              name.toString,
              _: Position
            )
          )
        case _ => ???
      }
    }

  object Implicit {
    implicit val xmlSimRepresentation: SimRepresentation[xml.Elem] =
      XMLSimRepresentation
  }
}
