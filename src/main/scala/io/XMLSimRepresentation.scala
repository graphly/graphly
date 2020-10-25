package io

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import scala.collection.mutable
import model.sim.{Connection, Measure, Node, Position, Sim, UserClass}
import model.sim
import scala.language.implicitConversions

object XMLSimRepresentation extends SimRepresentation[xml.Elem] {
  override def represent(x: Sim): xml.Elem = {
    val timestamp: String = DateTimeFormatter.ofPattern("E LLL D H:m:s zz u").format(ZonedDateTime.now())
    // TODO: This will almost certainly need it's own function once nodes are fully implemented
    val nodes: Array[xml.Elem] = x.nodes.map((node: Node) =>
      <node name="TODO" ></node>
    ).toArray

    val nodePositions: Array[xml.Elem] = x.nodes.map((node: Node) =>
      <station name="TODO">
        <position rotate="false" x={node.position.x.toString} y={node.position.y.toString}/>
      </station>
    ).toArray

    val connections: Array[xml.Elem] = x.connections.map((connection: Connection) =>
        <connection source={connection.source.toString} target={connection.target.toString}/>
    ).toArray

    // TODO: This is a temp hack :))
    <archive name="TODO" timestamp={timestamp} xsi:noNamespaceSchemaLocation="Archive.xsd">
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

  override def toSim(xmlSim: xml.Elem): Sim = {

  private def measureFromXML(xmlMeasure: xml.Node, nodes: collection.Map[String, Node], userClasses: collection.Map[String, UserClass]): Option[Measure] = for {
    classAlpha <- xmlMeasure.attribute("alpha")
    classReferenceNode <- xmlMeasure.attribute("referenceNode")
    classReferenceClass <- xmlMeasure.attribute("referenceUserClass")
    classType <- xmlMeasure.attribute("type")
    classVerbose <- xmlMeasure.attribute("verbose")
    referenceNode <- nodes.get(classReferenceNode.toString())
    referenceUserClass <- userClasses.get(classReferenceClass.toString())
  } yield Measure(classAlpha.toString().toFloat, referenceNode, referenceUserClass, classType.toString(), classVerbose.toString.toBoolean)


  private def userClassFromXML(xmlUserClass: xml.Node, nodes: collection.Map[String, Node]): Option[(String, UserClass)] = for {
    className <- xmlUserClass.attribute("name")
    classPriority <- xmlUserClass.attribute("priority")
    classReferenceSource <- xmlUserClass.attribute("referenceSource")
    classUserClassType <- xmlUserClass.attribute("type")
    priority <- classPriority.headOption
    referenceSource <- nodes.get(classReferenceSource.toString())
  } yield (className.toString(), UserClass(className.toString(), priority.toString().toInt, referenceSource, if (classUserClassType.toString().equals("open")) UserClass.Open else UserClass.Closed))

  private def connectionFromXML(xmlConnection: xml.Node, nodes: collection.Map[String, Node]): Option[Connection] = for {
    sourceName <- xmlConnection.attribute("source")
    targetName <- xmlConnection.attribute("target")
    sourceNode <- nodes.get(sourceName.toString)
    targetNode <- nodes.get(targetName.toString)
  } yield Connection(sourceNode, targetNode)

  private def nodeFromXML(xmlNode: xml.Node): Option[(String, Position => Node)] = for {
    classNames <- xmlNode.child(1).attribute("className")
    className  <- classNames.headOption
    names <- xmlNode.attribute("name")
    name <- names.headOption
  } yield className.toString match {
    //TODO: Add names & other node types
    case "Queue" => (name.toString, sim.Queue(_: Position))
    case "RandomSource" => (name.toString, sim.Source(_: Position))
    case "JobSink" => (name.toString, sim.Sink(_: Position))
  }

  object Implicit {
    implicit val xmlSimRepresentation: SimRepresentation[xml.Elem] = XMLSimRepresentation
  }
}