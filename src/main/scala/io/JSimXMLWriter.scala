package io

import java.util
import java.util.{List, Vector}

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
  ): Unit         = {
    val stations      = model.nodes
    var elem: Element = null

    for (station <- stations) {
      elem = doc.createElement(XML_E_STATION)
      elem.setAttribute(XML_A_STATION_NAME, station.name)

      station.nodeType match {
        case source: Source => writeSourceSection(doc, elem, model, source)
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
  ): Unit         = {
    val elem: Element                    = doc.createElement(XML_E_STATION_SECTION)
    elem.setAttribute(XML_A_STATION_SECTION_CLASSNAME, CLASSNAME_SOURCE)
    node.appendChild(elem)
//TODO: Userclasses may need reworking
    val userClasses                      = model.classes.toArray
    // obtain classes that must be generated by this source
    val distrParams: Array[XMLParameter] =
      new Array[XMLParameter](userClasses.length)

    for (i <- distrParams.indices) {
      // if current class must be generated by this source
      val currentClass = userClasses(i)
      if (station.userClasses.contains(currentClass))
        distrParams(i) = DistributionWriter.getDistributionParameter(
          currentClass.distribution,
          model,
          currentClass
        )
      else { // otherwise write a null parameter
        val name = "ServiceTimeStrategy"
        distrParams(i) = new XMLParameter(
          name,
          strategiesClasspathBase + serviceStrategiesSuffix + name,
          currentClass.name,
          "null",
          true
        )
      }
    }
    // creating global service strategy parameter
    val gspName: String = "ServiceStrategy"
    val globalStrategyParameter: XMLParameter = new XMLParameter(
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

  def getDistributionParameter(
      distr: Distribution,
      model: Sim,
      userClass: UserClass
  ): XMLParameter = {
    //TODO: other overloaded func
    val distribution: Array[XMLParameter] = getDistributionParameter(distr)
    //TODO: overloaded constructor
    val returnValue: XMLParameter         = new XMLParameter(
      "ServiceTimeStrategy",
      strategiesClasspathBase + serviceStrategiesSuffix + "ServiceTimeStrategy",
      userClass.name,
      Array[XMLParameter](distribution(0), distribution(1)),
      true
    )
    /*
     * although this parameter contains several others, array attribute
     * must be set to "false", as their type are not necessarily equal
     */
    returnValue.parameterArray = "false"
    returnValue
  }

  object DistributionWriter {
    /*
     * returns a distribution in XMLParameter format, to allow nesting it in
     * other parameters.
     */
    def getDistributionParameter(
        distr: Distribution,
        model: Sim,
        userClass: UserClass
    ): XMLParameter                                                = {
      val distribution = getDistributionParameter(distr)
      val returnValue  = new XMLParameter(
        "ServiceTimeStrategy",
        strategiesClasspathBase + serviceStrategiesSuffix +
          "ServiceTimeStrategy",
        userClass.name,
        Array[XMLParameter](distribution(0), distribution(1)),
        true
      )
      /*
       * although this parameter contains several others, array attribute
       * must be set to "false", as their type are not necessarily equal
       */
      returnValue.parameterArray = "false"
      returnValue
    }

    /**
      * Returns a Distribution in XMLParameter format without refclass. This
      * is used to write load dependent service section distributions
      *
      * @param distr
      * distribution to be written
      * @return the two object to represent a distribution: distribution and
      *         its parameter object Author: Bertoli Marco
      */
    private[xml] def getDistributionParameter(distr: Distribution) = { // a list of direct parameter -> parameter which must be passed
      // directly to the distribution object
      val directParams    = new util.Vector[XMLParameter]
      // a list of parameters which are passed to the distribution
      // parameter
      val nonDirectParams = new util.Vector[XMLParameter]
      var distrPar        = null
      // Object valueObj;
      // parse over all parameters and add them to the appropriate list
      for (i <- 0 until distr.getNumberOfParameters) {
        distrPar = distr.getParameter(i)
        if (distrPar.isDirectParameter) directParams.add(getParameter(distrPar))
        else nonDirectParams.add(getParameter(distrPar))
      }
      // get an array of the direct parameters
      val directPars = new Array[XMLParameter](directParams.size)
      for (i <- 0 until directPars.length) {
        directPars(i) = directParams.get(i)
      }
      // get an array of the non direct parameters
      val nonDirectPars = new Array[XMLParameter](nonDirectParams.size)
      for (i <- 0 until nonDirectPars.length) {
        nonDirectPars(i) = nonDirectParams.get(i)
      }
      // create the distribution parameter with the direct parameters
      val ret = new Array[XMLParameter](2)
      ret(0) = new XMLParameter(
        distr.getName,
        distr.getClassPath,
        null.asInstanceOf[String],
        directPars,
        true
      )
      // create the distribution parameter with the non direct parameters
      ret(1) = new XMLParameter(
        "distrPar",
        distr.getParameterClassPath,
        null,
        nonDirectPars,
        true
      )
      ret(0).parameterArray = "false"
      ret(1).parameterArray = "false"
      ret
    }

    /**
      * Helper method to extract an XMLParameter from a Distribution
      * parameter
      *
      * @param distrPar
      * the distribution parameter
      * @return the created XML Parameter
      */
    private[xml] def getParameter(
        distrPar: Distribution#Parameter
    ): XMLParameter                                                = {
      val valueObj = distrPar.getValue
      if (valueObj != null) if (valueObj.isInstanceOf[Distribution]) {
        val distribution =
          getDistributionParameter(valueObj.asInstanceOf[Distribution])
        val returnValue  = new XMLParameter(
          distrPar.getName,
          distributionContainerClasspath,
          null,
          Array[XMLParameter](distribution(0), distribution(1)),
          true
        )
        /*
         * although this parameter contains several others, array
         * attribute must be set to "false", as their type are not
         * necessarily equal
         */
        returnValue.parameterArray = "false"
        return returnValue
      } else if (valueObj.isInstanceOf[Array[Array[AnyRef]]]) {
        val value  = valueObj.asInstanceOf[Array[Array[AnyRef]]]
        val vector = new Array[XMLParameter](value.length)
        for (i <- 0 until vector.length) {
          val entry = new Array[XMLParameter](value(i).length)
          for (j <- 0 until entry.length) {
            entry(j) = new XMLParameter(
              "entry",
              distrPar.getValueClass.getName,
              null,
              value(i)(j).toString,
              true
            )
          }
          vector(i) =
            new XMLParameter("vector", classOf[Any].getName, null, entry, true)
        }
        return new XMLParameter(
          distrPar.getName,
          classOf[Any].getName,
          null,
          vector,
          true
        )
      } else return new XMLParameter(distrPar.getName, distrPar.getValueClass.getName, null, valueObj.toString, true)
      null
    }
  }

  case class XMLParameter(
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
