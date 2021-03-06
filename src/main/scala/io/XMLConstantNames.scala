package io

object XMLConstantNames {
  val XML_DOCUMENT_XSD                       = "SIMmodeldefinition.xsd"
  val XML_DOCUMENT_ROOT                      = "sim"
  val XML_A_ROOT_NAME                        = "name"
  val XML_A_ROOT_SEED                        = "seed"
  val XML_A_ROOT_DURATION                    = "maxTime"
  val XML_A_ROOT_SIMULATED                   = "maxSimulated"
  val XML_A_ROOT_LOGPATH                     = "logPath" /* MF08 0.7.4 - path of logs */
  val XML_A_ROOT_LOGDELIM                    =
    "logDelimiter" /* MF08 0.7.4 - delimiter character in log */
  val XML_A_ROOT_LOGDECIMALSEPARATOR         = "logDecimalSeparator"
  val XML_A_ROOT_LOGREPLACE                  =
    "logReplaceMode" /* MF08 0.7.4 - replacement mode */
  val XML_A_ROOT_POLLING                     = "polling"
  val XML_A_ROOT_MAXSAMPLES                  = "maxSamples"
  val XML_A_ROOT_DISABLESTATISTIC            = "disableStatisticStop"
  val XML_A_ROOT_MAXEVENTS                   = "maxEvents"
  val XML_E_CLASS                            = "userClass"
  val XML_A_CLASS_NAME                       = "name"
  val XML_A_CLASS_TYPE                       = "type"
  val XML_A_CLASS_PRIORITY                   = "priority"
  val XML_A_CLASS_REFSOURCE                  = "referenceSource"
  val XML_A_CLASS_CUSTOMERS                  = "customers"
  val XML_E_STATION                          = "node"
  val XML_A_STATION_NAME                     = "name"
  val XML_E_STATION_SECTION                  = "section"
  val XML_A_STATION_SECTION_CLASSNAME        = "className"
  val XML_E_PARAMETER                        = "parameter"
  val XML_A_PARAMETER_NAME                   = "name"
  val XML_A_PARAMETER_CLASSPATH              = "classPath"
  val XML_A_PARAMETER_ARRAY                  = "array"
  val XML_A_PARAMETER_DIRECT                 = "isDirect"
  val XML_E_PARAMETER_VALUE                  = "value"
  val XML_E_PARAMETER_REFCLASS               = "refClass"
  val XML_E_SUBPARAMETER                     = "subParameter"
  val XML_A_SUBPARAMETER_NAME                = "name"
  val XML_A_SUBPARAMETER_CLASSPATH           = "classPath"
  val XML_A_SUBPARAMETER_ARRAY               = "array"
  val XML_E_SUBPARAMETER_VALUE               = "value"
  val XML_E_MEASURE                          = "measure"
  val XML_A_MEASURE_NAME                     = "name"
  val XML_A_MEASURE_ALPHA                    = "alpha"
  val XML_A_MEASURE_PRECISION                = "precision"
  val XML_A_MEASURE_VERBOSE                  = "verbose"
  val XML_A_MEASURE_TYPE                     = "type"
  val XML_A_MEASURE_CLASS                    = "referenceUserClass"
  val XML_A_MEASURE_STATION                  = "referenceNode"
  val XML_A_MEASURE_NODETYPE                 = "nodeType"
  val XML_A_MEASURE_LOG                      = "logFile"
  val XML_E_CONNECTION                       = "connection"
  val XML_A_CONNECTION_SOURCE                = "source"
  val XML_A_CONNECTION_TARGET                = "target"
  val XML_E_PRELOAD                          = "preload"
  val XML_E_STATIONPOPULATIONS               = "stationPopulations"
  val XML_A_PRELOADSTATION_NAME              = "stationName"
  val XML_E_CLASSPOPULATION                  = "classPopulation"
  val XML_A_CLASSPOPULATION_NAME             = "refClass"
  val XML_A_CLASSPOPULATION_POPULATION       = "population"
  val XML_LOG_FILENAME                       = "logfileName"
  val XML_LOG_FILEPATH                       = "logfilePath"
  val XML_LOG_B_EXECTIMESTAMP                = "logExecTimestamp"
  val XML_LOG_B_LOGGERNAME                   = "logLoggerName"
  val XML_LOG_B_TIMESTAMP                    = "logTimeStamp"
  val XML_LOG_B_JOBID                        = "logJobID"
  val XML_LOG_B_JOBCLASS                     = "logJobClass"
  val XML_LOG_B_TIMESAMECLS                  = "logTimeSameClass"
  val XML_LOG_B_TIMEANYCLS                   = "logTimeAnyClass"
  val XML_E_REGION                           = "blockingRegion"
  val XML_A_REGION_NAME                      = "name"
  val XML_A_REGION_TYPE                      = "type"
  val XML_E_REGIONNODE                       = "regionNode"
  val XML_A_REGIONNODE_NAME                  = "nodeName"
  val XML_E_GLOBALCONSTRAINT                 = "globalConstraint"
  val XML_A_GLOBALCONSTRAINT_MAXJOBS         = "maxJobs"
  val XML_E_GLOBALMEMORYCONSTRAINT           = "globalMemoryConstraint"
  val XML_A_GLOBALMEMORYCONSTRAINT_MAXMEMORY = "maxMemory"
  val XML_E_CLASSCONSTRAINT                  = "classConstraint"
  val XML_A_CLASSCONSTRAINT_CLASS            = "jobClass"
  val XML_A_CLASSCONSTRAINT_MAXJOBS          = "maxJobsPerClass"
  val XML_E_CLASSMEMORYCONSTRAINT            = "classMemoryConstraint"
  val XML_A_CLASSMEMORYCONSTRAINT_CLASS      = "jobClass"
  val XML_A_CLASSMEMORYCONSTRAINT_MAXMEMORY  = "maxMemoryPerClass"
  val XML_E_DROPRULES                        = "dropRules"
  val XML_A_DROPRULE_CLASS                   = "jobClass"
  val XML_A_DROPRULE_DROP                    = "dropThisClass"
  val XML_E_CLASSWEIGHT                      = "classWeight"
  val XML_A_CLASSWEIGHT_CLASS                = "jobClass"
  val XML_A_CLASSWEIGHT_WEIGHT               = "weight"
  val XML_E_CLASSSIZE                        = "classSize"
  val XML_A_CLASSSIZE_CLASS                  = "jobClass"
  val XML_A_CLASSSIZE_SIZE                   = "size"
  val XML_E_GROUPCONSTRAINT                  = "groupConstraint"
  val XML_A_GROUPCONSTRAINT_GROUP            = "jobGroup"
  val XML_A_GROUPCONSTRAINT_MAXJOBS          = "maxJobsPerGroup"
  val XML_E_GROUPMEMORYCONSTRAINT            = "groupMemoryConstraint"
  val XML_A_GROUPMEMORYCONSTRAINT_GROUP      = "jobGroup"
  val XML_A_GROUPMEMORYCONSTRAINT_MAXMEMORY  = "maxMemoryPerGroup"
  val XML_E_GROUPCLASSLIST                   = "groupClassList"
  val XML_E_GROUPCLASS                       = "groupClass"
  val XML_A_GROUPCLASS_GROUP                 = "jobGroup"
  val XML_A_GROUPCLASS_CLASS                 = "jobClass"
  val CLASSNAME_SOURCE                       = "RandomSource"
  val CLASSNAME_TERMINAL                     = "Terminal"
  val CLASSNAME_QUEUE                        = "Queue"
  val CLASSNAME_SINK                         = "JobSink"
  val CLASSNAME_SERVER                       = "Server"
  val CLASSNAME_PSSERVER                     = "PSServer"
  val CLASSNAME_DELAY                        = "Delay"
  val CLASSNAME_TUNNEL                       = "ServiceTunnel"
  val CLASSNAME_LOGGER                       = "LogTunnel" /* MF08 0.7.4 - extends ServiceTunnel */
  val CLASSNAME_ROUTER                       = "Router"
  val CLASSNAME_FORK                         = "Fork"
  val CLASSNAME_JOIN                         = "Join"
  val CLASSNAME_CLASSSWITCH                  = "ClassSwitch"
  val CLASSNAME_SEMAPHORE                    = "Semaphore"
  val CLASSNAME_STORAGE                      = "Storage"
  val CLASSNAME_LINKAGE                      = "Linkage"
  val CLASSNAME_ENABLING                     = "Enabling"
  val CLASSNAME_TIMING                       = "Timing"
  val CLASSNAME_FIRING                       = "Firing"
  val XML_E_MAPREDUCE                        = "template_mapreduce"
  val NODETYPE_REGION                        = "region"
  val NODETYPE_STATION                       = "station"
  val XML_E_TRACE                            = "trace"
  val XML_A_TRACE_X                          = "x"
  val XML_A_TRACE_Y                          = "y"
  val XML_A_TRACE_WIDTH                      = "width"
  val XML_A_TRACE_HEIGHT                     = "height"
  val XML_E_RESULTS                          = "results"

  /**
    * Parser features
    */
  val VALIDATION_FEATURE_ID                = "http://xml.org/sax/features/validation"
  val SCHEMA_VALIDATION_FEATURE_ID         =
    "http://apache.org/xml/features/validation/schema"
  val VALIDATION_DYNAMIC_FEATURE_ID        =
    "http://apache.org/xml/features/validation/dynamic"
  val NAMESPACES_FEATURE_ID                = "http://xml.org/sax/features/namespaces"
  val EXTERNAL_SCHEMA_LOCATION_PROPERTY_ID =
    "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation"

  val strategiesClasspathBase        = "jmt.engine.NetStrategies."
  val psStrategiesSuffix             = "PSStrategies."
  val queueGetStrategiesSuffix       = "QueueGetStrategies."
  val queuePutStrategiesSuffix       = "QueuePutStrategies."
  val serviceStrategiesSuffix        = "ServiceStrategies."
  val routingStrategiesSuffix        = "RoutingStrategies."
  val transitionUtilitiesSuffix      = "TransitionUtilities."
  val distributionContainerClasspath = "jmt.engine.random.DistributionContainer"

//GUI Constants
  val XML_A_CLASS_COLOR = "color"

  val XML_E_POSITION        = "position"
  val XML_A_POSITION_X      = "x"
  val XML_A_POSITION_Y      = "y"
  val XML_A_POSITION_ROTATE = "rotate"
  val XML_A_POSITION_ANGLE  = "angle"

  val XML_E_PARAMETRIC           = "parametric"
  val XML_A_PARAMETRIC_CLASSPATH = "classPath"
  val XML_A_PARAMETRIC_ENABLED   = "enabled"
  val XML_E_FIELD                = "field"
  val XML_A_FIELD_NAME           = "name"
  val XML_A_FIELD_VALUE          = "value"

  val XML_ARCHIVE_DOCUMENT_XSD  = "Archive.xsd"
  val XML_ARCHIVE_DOCUMENT_ROOT = "archive"

  val XML_ARCHIVE_A_NAME      = "name"
  val XML_ARCHIVE_A_TIMESTAMP = "timestamp"

  val XML_E_CONNECTION_SHAPE = "connectionShape"

  val XML_E_ARC = "arc"

  val XML_E_POINT   = "point"
  val XML_A_POINT_X = "x"
  val XML_A_POINT_Y = "y"

  val XML_E_SOURCE   = "source"
  val XML_A_SOURCE_X = "x"
  val XML_A_SOURCE_Y = "y"

  val XML_E_TARGET   = "target"
  val XML_A_TARGET_X = "x"
  val XML_A_TARGET_Y = "y"

}
