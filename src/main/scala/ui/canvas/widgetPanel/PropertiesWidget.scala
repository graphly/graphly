package ui.canvas.widgetPanel

import model.sim.Sim

class PropertiesWidget(title: String, model: Sim)
    extends Widget(title: String) {
  managed <== visible
  private val propertiesPanel = new PropertiesPanel(model)
  center = propertiesPanel

  def generateGlobalMenu(): Unit = {
    longField("Seed", model.configuration.seed, _.configuration.seed = _, Some("Sample text"))
    checkbox("Use random seed", model.configuration.useRandomSeed, _.configuration.useRandomSeed = _)
    doubleField("Maximum duration", model.configuration.maximumDuration, _.configuration.maximumDuration = _)
    doubleField("Max simulated time", model.configuration.maxSimulatedTime, _.configuration.maxSimulatedTime = _)
    integerField("Max samples", model.configuration.maxSamples, _.configuration.maxSamples = _)
    checkbox("Disable statistic", model.configuration.disableStatistic, _.configuration.disableStatistic = _)
    integerField("Max events", model.configuration.maxEvents, _.configuration.maxEvents = _)
    doubleField("Polling interval", model.configuration.pollingInterval, _.configuration.pollingInterval = _)
//    checkbox("Parametric analysis enabled", model.configuration.parametricAnalysisEnabled, _.configuration.parametricAnalysisEnabled = _)
    textField("Logging path", model.configuration.loggingPath, _.configuration.loggingPath = _)
    textField("Logging auto append", model.configuration.loggingAutoAppend, _.configuration.loggingAutoAppend = _)
    textField("Logging delim", model.configuration.loggingDelim, _.configuration.loggingDelim = _)
    textField("Logging decimal separator", model.configuration.loggingDecimalSeparator, _.configuration.loggingDecimalSeparator = _)
  }

  def clear(): Unit = { propertiesPanel.clearAll() }

  def textField(
      title: String,
      initial: String,
      configApplication: (Sim, String) => Unit,
      tooltip: Option[String] = Option.empty
  ): Unit = propertiesPanel.textField(title, initial, configApplication, tooltip)
  def integerField(
      title: String,
      initial: Int,
      configApplication: (Sim, Int) => Unit,
      tooltip: Option[String] = Option.empty
  ): Unit = propertiesPanel.integerField(title, initial, configApplication, tooltip)
  def longField(
      title: String,
      initial: Long,
      configApplication: (Sim, Long) => Unit,
      tooltip: Option[String] = Option.empty
  ): Unit = propertiesPanel.longField(title, initial, configApplication, tooltip)
  def doubleField(
      title: String,
      initial: Double,
      configApplication: (Sim, Double) => Unit,
      tooltip: Option[String] = Option.empty
  ): Unit = propertiesPanel.doubleField(title, initial, configApplication, tooltip)
  def dropdown(
      title: String,
      options: List[String],
      placeholder: String,
      configApplication: (Sim, String) => Unit,
      tooltip: Option[String] = Option.empty
  ): Unit =
    propertiesPanel.dropdown(title, options, placeholder, configApplication, tooltip)
  def checkbox(
      title: String,
      initial: Boolean,
      configApplication: (Sim, Boolean) => Unit,
      tooltip: Option[String] = Option.empty
  ): Unit = { propertiesPanel.checkbox(title, initial, configApplication, tooltip) }
}
