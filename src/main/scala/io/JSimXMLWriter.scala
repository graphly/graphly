package io

import io.XMLConstantNames._
import javax.xml.parsers.DocumentBuilderFactory
import model.sim.UserClass.{Closed, Open}
import model.sim.{Sim, _}
import org.w3c.dom.{Document, Element, Node}

object JSimXMLWriter extends SimRepresentation[Document] {

  protected def writeModel(model: Sim, modelName: String): Unit = {
    val dbf        = DocumentBuilderFactory.newInstance
    val docBuilder = dbf.newDocumentBuilder
    val modelDoc   = docBuilder.newDocument

    val elem = modelDoc.createElement(XML_DOCUMENT_ROOT)
    modelDoc.appendChild(elem)
    elem.setAttribute(XML_A_ROOT_NAME, modelName)
    elem.setAttribute("xsi:noNamespaceSchemaLocation", XML_DOCUMENT_XSD)
    elem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance")
    // Simulation seed
    if (!model.useRandomSeed)
      elem.setAttribute(XML_A_ROOT_SEED, model.seed.toString)
    // Max simulation time
    if (model.maximumDuration > 0)
      elem.setAttribute(XML_A_ROOT_DURATION, model.maximumDuration.toString)
    // Max simulated time (not real time, but time in system of the simulated process)
    if (model.maxSimulatedTime.doubleValue > 0)
      elem.setAttribute(XML_A_ROOT_SIMULATED, model.maxSimulatedTime.toString)
    // Polling interval
    elem.setAttribute(XML_A_ROOT_POLLING, model.pollingInterval.toString)
    // Max samples
    elem.setAttribute(XML_A_ROOT_MAXSAMPLES, model.maxSamples.toString)
    // Disable statistic
    elem.setAttribute(
      XML_A_ROOT_DISABLESTATISTIC,
      model.disableStatistic.toString
    )
    // Max events
    elem.setAttribute(XML_A_ROOT_MAXEVENTS, model.maxEvents.toString)
    // Write attributes used by the logs - Michael Fercu
    elem.setAttribute(
      XML_A_ROOT_LOGPATH,
      System.getProperty("user.home").concat("/JMT/")
      //TODO
//      MacroReplacer.replace(model.getLoggingGlbParameter("path"))
    )
    elem.setAttribute(
      XML_A_ROOT_LOGREPLACE,
      0.toString
      //TODO
//      model.getLoggingGlbParameter("autoAppend")
    )
    elem.setAttribute(
      XML_A_ROOT_LOGDELIM,
      ","
      //TODO
//        model.getLoggingGlbParameter("delim")
    )
    elem.setAttribute(
      XML_A_ROOT_LOGDECIMALSEPARATOR,
      "."
      //TODO
//      model.getLoggingGlbParameter("decimalSeparator")
    )
    // Manage probabilities
    //TODO
//    model.manageProbabilities()
    // Write all elements
    writeClasses(modelDoc, elem, model)
//    writeStations(modelDoc, elem, model)
//    writeMeasures(modelDoc, elem, model)
//    writeConnections(modelDoc, elem, model)
//    writeBlockingRegions(modelDoc, elem, model)
//    writePreload(modelDoc, elem, model)
  }

  protected def writeClasses(
      doc: Document,
      simNode: Element,
      model: Sim
  ): Unit                                                       = {

    for (userClass <- model.classes) {
      val classType: String = userClass.`type` match {
        case Open => "open"
        case Closed => "closed"
      }

      val userClassElem: Element     = doc.createElement(XML_E_CLASS)
      val attrsNames: Array[String]  = Array(
        XML_A_CLASS_NAME,
        XML_A_CLASS_TYPE,
        XML_A_CLASS_PRIORITY,
        XML_A_CLASS_CUSTOMERS,
        XML_A_CLASS_REFSOURCE
      )
      val attrsValues: Array[String] = Array(
        userClass.name,
        classType,
        String.valueOf(userClass.priority),
        String.valueOf(userClass.population),
        userClass.referenceSource
      )
      for (j <- attrsNames.indices) {
        if (attrsValues(j) != null && !("null" == attrsValues(j))) {
          userClassElem.setAttribute(attrsNames(j), attrsValues(j))
        }
      }
      simNode.appendChild(userClassElem)
    }
  }

  protected def writeStations(
      doc: Document,
      simNode: Node,
      model: Sim
  ): Unit = {
    val stations      = model.nodes
    var elem: Element = null

    for (station <- stations) {
      elem = doc.createElement(XML_E_STATION)
      elem.setAttribute(XML_A_STATION_NAME, station.name)

      station.nodeType match {
        case Source() => writeSourceSection(doc, elem, model, station)
//          writeTunnelSection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case Sink() =>
//          writeSinkSection(doc, elem, model, stationKey)
        case Terminal() =>
//          writeTerminalSection(doc, elem, model, stationKey)
//          writeTunnelSection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case Router() =>
//          writeQueueSection(doc, elem, model, stationKey)
//          writeTunnelSection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case Delay() =>
//          writeQueueSection(doc, elem, model, stationKey)
//          writeDelaySection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case Server() =>
//          writeQueueSection(doc, elem, model, stationKey)
//          writeServerSection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case Fork() =>
//          writeQueueSection(doc, elem, model, stationKey)
//          writeTunnelSection(doc, elem, model, stationKey)
//          writeForkSection(doc, elem, model, stationKey)
        case Join() =>
//          writeJoinSection(doc, elem, model, stationKey)
//          writeTunnelSection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case Logger() =>
//          writeQueueSection(doc, elem, model, stationKey)
//          writeLoggerSection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case ClassSwitch() =>
//          writeQueueSection(doc, elem, model, stationKey)
//          writeClassSwitchSection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case Semaphore() =>
//          writeSemaphoreSection(doc, elem, model, stationKey)
//          writeTunnelSection(doc, elem, model, stationKey)
//          writeRouterSection(doc, elem, model, stationKey)
        case Scalar() =>
//          writeJoinSection(doc, elem, model, stationKey)
//          writeTunnelSection(doc, elem, model, stationKey)
//          writeForkSection(doc, elem, model, stationKey)
        case Place() =>
//          writeStorageSection(doc, elem, model, stationKey)
//          writeTunnelSection(doc, elem, model, stationKey)
//          writeLinkageSection(doc, elem, model, stationKey)
        case Transition() =>
//          writeEnablingSection(doc, elem, model, stationKey)
//          writeTimingSection(doc, elem, model, stationKey)
//          writeFiringSection(doc, elem, model, stationKey)
      }
      simNode.appendChild(elem)
    }
  }

  protected def writeSourceSection(
      doc: Document,
      node: Node,
      model: Sim,
      station: Source
  ): Unit = {
    val elem: Element                    = doc.createElement(XML_E_STATION_SECTION)
    elem.setAttribute(XML_A_STATION_SECTION_CLASSNAME, CLASSNAME_SOURCE)
    node.appendChild(elem)
//TODO: Userclasses may need reworking
    val userClasses                      = station.userClasses
    // obtain classes that must be generated by this source
    val distrParams: Array[XMLParameter] =
      new Array[XMLParameter](userClasses.size)

    for (i <- distrParams.indices) {
      // if current class must be generated by this source
      val currentClass = userClasses(i)
      if (refClasses.contains(currentClass))
        distrParams(i) = DistributionWriter.getDistributionParameter(
          model.getClassDistribution(currentClass).asInstanceOf[Distribution],
          model,
          currentClass
        )
      else { // otherwise write a null parameter
        val name = "ServiceTimeStrategy"
        distrParams(i) = new XMLWriter.XMLParameter(
          name,
          strategiesClasspathBase + serviceStrategiesSuffix + name,
          model.getClassName(currentClass),
          "null",
          true
        )
      }
    }
    // creating global service strategy parameter
    val gspName: String = "ServiceStrategy"
    val globalStrategyParameter: XMLWriter.XMLParameter =
      new XMLWriter.XMLParameter(
        gspName,
        strategiesClasspathBase + gspName,
        null,
        distrParams,
        false
      )
    // finally, create node from parameters and append it to the section
    // element
    globalStrategyParameter.appendParameterElement(doc, elem)
  }

  case class XMLParameter private (
      var parameterName: String,
      var parameterClasspath: String,
      var parameterRefClass: String,
      var parameterValue: String,
      var parameterArray: String,
      var parameters: Array[XMLParameter],
      var isSubParameter: Boolean = false
  ) {
//TODO: Constructors

    def appendParameterElement(doc: Document, scope: Element): Unit = {
      // creating inner element containing queue length
      val parameter = doc.createElement(
        if (isSubParameter) XML_E_SUBPARAMETER else XML_E_PARAMETER
      )
      if (parameterClasspath != null)
        parameter.setAttribute(XML_A_PARAMETER_CLASSPATH, parameterClasspath)
      if (parameterName != null)
        parameter.setAttribute(XML_A_PARAMETER_NAME, parameterName)
      if (parameterArray != null && "true" == parameterArray)
        parameter.setAttribute(XML_A_PARAMETER_ARRAY, parameterArray)
      // adding element refclass for this parameter
      if (parameterRefClass != null) {
        val refclass = doc.createElement(XML_E_PARAMETER_REFCLASS)
        refclass.appendChild(doc.createTextNode(parameterRefClass))
        scope.appendChild(refclass)
      }
      // adding element value of parameter
      if (parameterValue != null) {
        val value = doc.createElement(XML_E_PARAMETER_VALUE)
        value.appendChild(doc.createTextNode(parameterValue))
        parameter.appendChild(value)
      }
      if (parameters != null) for (parameter2 <- parameters) {
        if (parameter2 != null)
          parameter2.appendParameterElement(doc, parameter)
      }
      scope.appendChild(parameter)
    }
  }

}
