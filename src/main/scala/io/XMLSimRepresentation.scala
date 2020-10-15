package io

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import model.sim.{Connection, Node, Sim}

object XMLSimRepresentation extends SimRepresentation[xml.Elem] {
  override def represent(x: Sim): xml.Elem = {
    val timestamp: String = DateTimeFormatter.ofPattern("E LLL D H:m:s zz u").format(ZonedDateTime.now())
    // TODO: This will almost certainly need it's own function once nodes are fully implemented
    val nodes: Array[xml.Elem] = x.nodes.map((node: Node) =>
      <node name="TODO"></node>
    ).toArray

    val nodePositions: Array[xml.Elem] = x.nodes.map((node: Node) =>
      <station name="TODO">
        <position rotate="false" x="0.0" y="0.0"/>
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

  implicit val xmlSimRepresentation: SimRepresentation[xml.Elem] = this
}