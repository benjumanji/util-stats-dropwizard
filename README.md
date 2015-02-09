Drowpwizard StatsReceiver for use with twitter-util StatsRecievers
==================================================================

There is one major use case this is designed for: finagle. To use with finagle simply include the library on the class path, it will be loaded automatically via the `LoadService` class and added to the DefaultReciever. You can then use the default reciever safe in the knowledge that all your stats are being collected into a Drowpwizard metrics registry. To get them back out again simply grab the registry from the companion object and use as you would any other registry for reporting. An example

```
// other imports...

import io.artfuldodge.util.stats.MetricsStatsReceiver

object FinagleThing extends App {

  val service = new Service[Request, Response] {
    def apply(req: Request) = {
      Future.const(Response(Status.Ok))
    }
  }

  val server = Httpx.server(":*", service)
  val registry = MetricsStatsReceiver.registry

  val reporter = ConsoleReporter.forRegistry(registry)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()
  reporter.start(1, TimeUnit.SECONDS)

  Await.ready(server)
}
```

And you will start seeing all the lovely stats that a finagle server exports be default appearing in your console.

To add to your sbt project:

```
resolvers += "benjumanji" at "http://dl.bintray.com/benjumanji/maven"
libraryDependencies += "io.artfuldodge" %% "util-stats-dropwizard" % "0.2"
```
