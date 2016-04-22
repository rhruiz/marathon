package mesosphere.marathon.core.election

import akka.actor.ActorSystem
import akka.event.EventStream
import com.codahale.metrics.MetricRegistry
import com.twitter.common.zookeeper.ZooKeeperClient
import mesosphere.chaos.http.HttpConf
import mesosphere.marathon.MarathonConf
import mesosphere.marathon.core.election.impl.{ TwitterCommonsElectionService, PseudoElectionService }
import mesosphere.marathon.metrics.Metrics

import scala.collection.immutable.Seq

class ElectionModule(
    config: MarathonConf,
    system: ActorSystem,
    eventStream: EventStream,
    http: HttpConf,
    metrics: Metrics = new Metrics(new MetricRegistry),
    hostPort: String,
    zk: ZooKeeperClient,
    electionCallbacks: Seq[ElectionCallback] = Seq.empty,
    delegate: ElectionCandidate) {
  lazy val service = if (config.highlyAvailable()) {
    new TwitterCommonsElectionService(
      config,
      system,
      eventStream,
      http,
      metrics,
      hostPort,
      zk,
      electionCallbacks,
      delegate
    )
  }
  else {
    new PseudoElectionService(
      config,
      system,
      eventStream,
      metrics,
      hostPort,
      electionCallbacks,
      delegate
    )
  }
}