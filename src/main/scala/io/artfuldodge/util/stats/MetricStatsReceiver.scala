package io.artfuldodge.util.stats

import scala.collection.JavaConversions._

import com.codahale.metrics.{
  Counter => MetricsCounter,
  Gauge => MetricsGauge,
  Histogram => MetricsHistogram,
  Metric,
  MetricFilter,
  MetricRegistry
}
import com.twitter.finagle.stats.{
  Counter,
  Gauge,
  Stat,
  StatsReceiverWithCumulativeGauges
}

private[stats] class WrappedCounter(counter: MetricsCounter) extends Counter {
  def incr(delta: Int) = counter.inc(delta)
}

private[stats] class WrappedHistogram(histogram: MetricsHistogram) extends Stat {
  def add(value: Float) = histogram.update(value.toLong)
}

class MetricsStatsReceiver(metrics: MetricRegistry) extends StatsReceiverWithCumulativeGauges {
  import MetricsStatsReceiver.toDotted

  def this() = this(MetricsStatsReceiver.registry)


  val repr = this
  def registry: MetricRegistry = metrics

  def counter(name: String*): Counter = {
    new WrappedCounter(metrics.counter(toDotted(name)))
  }

  def stat(name: String*): Stat = {
    new WrappedHistogram(metrics.histogram(toDotted(name)))
  }

  protected[this] def registerGauge(names: Seq[String], f: => Float) {
    val dotted: String = toDotted(names)

    metrics.register(dotted, new MetricsGauge[Float] {
      def getValue(): Float = f
    })
  }

  protected[this] def deregisterGauge(names: Seq[String]) {
    metrics.remove(toDotted(names))
  }
}

object MetricsStatsReceiver {
  private[this] val metrics = new MetricRegistry
  private[stats] def toDotted(name: Seq[String]): String = name.mkString(".")

  def registry = metrics

  /**
   * Removes all gauges under a prefix.
   *
   * This only needs to be called if you are recycling names.
   * @param scope The scope under which you wish to remove the gauages.
   */
  def removeGauges(scope: Seq[String]) {
    val filter = new MetricFilter {
      val dotted = toDotted(scope)
      def matches(name: String, metric: Metric): Boolean = name.startsWith(dotted)
    }
    metrics.getGauges(filter) foreach { case (name, _) => metrics.remove(name) }
  }
}
