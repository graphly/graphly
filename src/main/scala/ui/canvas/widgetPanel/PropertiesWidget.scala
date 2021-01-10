package ui.canvas.widgetPanel

import model.sim.{Configuration, Sim}

class PropertiesWidget(title: String, model: Sim)
    extends Widget(title: String) {
  private val propertiesPanel = new PropertiesPanel(model)
  center = propertiesPanel

  def generateGlobalMenu(): Unit = {
    integerField("Seed", model.configuration.seed.toInt, _.seed = _)
    checkbox("Use random seed", model.configuration.useRandomSeed, _.useRandomSeed = _)
    doubleField("Maximum duration", model.configuration.maximumDuration, _.maximumDuration = _)
    doubleField("Max simulated time", model.configuration.maxSimulatedTime, _.maxSimulatedTime = _)
    integerField("Max samples", model.configuration.maxSamples, _.maxSamples = _)
    checkbox("Disable statistic", model.configuration.disableStatistic, _.disableStatistic = _)
    integerField("Max events", model.configuration.maxEvents, _.maxEvents = _)
    doubleField("Polling interval", model.configuration.pollingInterval, _.pollingInterval = _)
    checkbox("Parametric analysis enabled", model.configuration.parametricAnalysisEnabled, _.parametricAnalysisEnabled = _)
    textField("Logging path", model.configuration.loggingPath, _.loggingPath = _)
    textField("Logging auto append", model.configuration.loggingAutoAppend, _.loggingAutoAppend = _)
    textField("Logging delim", model.configuration.loggingDelim, _.loggingDelim = _)
    textField("Logging decimal separator", model.configuration.loggingDecimalSeparator, _.loggingDecimalSeparator = _)
  }

  def clear(): Unit = { propertiesPanel.clearAll() }

  def textField(
      title: String,
      initial: String,
      configApplication: (Configuration, String) => Unit
  ): Unit = propertiesPanel.textField(title, initial, configApplication)
  def integerField(
      title: String,
      initial: Int,
      configApplication: (Configuration, Int) => Unit
  ): Unit = propertiesPanel.integerField(title, initial, configApplication)
  def doubleField(
      title: String,
      initial: Double,
      configApplication: (Configuration, Double) => Unit
  ): Unit = propertiesPanel.doubleField(title, initial, configApplication)
  def dropdown(
      title: String,
      options: List[String],
      placeholder: String,
      configApplication: (Configuration, String) => Unit
  ): Unit =
    propertiesPanel.dropdown(title, options, placeholder, configApplication)
  def checkbox(title: String, initial: Boolean, configApplication: (Configuration, Boolean) => Unit): Unit = {
    propertiesPanel.checkbox(title, initial, configApplication)
  }
}
