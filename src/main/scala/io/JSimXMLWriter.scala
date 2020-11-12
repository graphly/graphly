package io

import io.XMLConstantNames._
import javax.xml.parsers.DocumentBuilderFactory
import model.sim.Sim
import org.w3c.dom.Document

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
}
