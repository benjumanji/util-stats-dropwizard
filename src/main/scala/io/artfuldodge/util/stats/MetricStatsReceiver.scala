package io.artfuldodge.util.stats

import com.codahale.metrics.{
  Counter => MetricsCounter,
  Gauge => MetricsGauge,
  Histogram => MetricsHistogram,
  MetricRegistry
}
import com.twitter.finagle.stats.{
  Counter,
  Gauge,
  Stat,
  StatsReceiver
}

private[stats] class WrappedCounter(counter: MetricsCounter) extends Counter {
  def incr(delta: Int) = counter.inc(delta)
}

private[stats] class WrappedHistogram(histogram: MetricsHistogram) extends Stat {
  def add(value: Float) = histogram.update(value.toLong)
}

private[stats] class WrappedGauage(metrics: MetricRegistry, name: String) extends Gauge
{
  def remove() { metrics.remove(name) }
}

class MetricsStatsReciever(metrics: MetricRegistry) extends StatsReceiver {

  private[this] def toDotted(name: Seq[String]): String = name.mkString(".")

  val repr = this
  def registry: MetricRegistry = metrics

  def counter(name: String*): Counter = {
    new WrappedCounter(metrics.counter(toDotted(name)))
  }

  def stat(name: String*): Stat = {
    new WrappedHistogram(metrics.histogram(toDotted(name)))
  }

  def addGauge(name: String*)(f: => Float): Gauge = {
    val dotted: String = toDotted(name)

    metrics.register(dotted, new MetricsGauge[Float] {
      def getValue(): Float = f
    })

    new WrappedGauage(metrics, dotted)
  }
}

object MetricsStatsReciever extends MetricsStatsReciever(new MetricRegistry)
