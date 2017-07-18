# Sumo Logic Logback Appender

Create a Logback compatible Appender for sending data to SumoLogic.com. This is useful if you run a
[Play Framework](https://playframework.com) or [Akka Server](http://akka.io/) both of which use Logback over Log4J.

#Installation

```scala
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.sonatypeRepo("snapshots")
libraryDependencies += "com.github.vital-software" %% "sumo-logback" % "0.2-SNAPSHOT"
```

In your logback.xml file:
```
 <appender name="SUMOLOGBACK" class="com.github.vitalsoftware.logging.SumoLogicAppender">
   <url>[collector URL created on SumoLogic.com]</url>
   <encoder>
     <pattern>%date{yyyy-MM-dd HH:mm:ss,SSS Z} [%level] from %logger - %message%n%xException</pattern>
   </encoder>
 </appender>
 
 <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
   <appender-ref ref="SUMOLOGBACK" />
 </appender>
 
 <root level="DEBUG">
   <appender-ref ref="ASYNC" />
 </root>
```

Be sure to wrap your appender in an `AsyncAppender` so that log events do not block the current thread.

## Dependencies
 - [Logback Core/Classic](https://logback.qos.ch)
 - [SumoLogic Log4J2](https://github.com/SumoLogic/sumologic-log4j2-appender)

## Future Work
SumoLogic supports proxy's, configurable timeouts, etc. This system hardcodes all the default values in the Log4J
implementation. If you need to configure these, create a set{VarName} method inside SumoLogicAppender.scala and
set an XML tag <varName>1234</varName>. Then be a pal and do a pull request for the rest of us.