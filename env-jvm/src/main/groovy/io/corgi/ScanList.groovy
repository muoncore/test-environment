package io.corgi

import io.muoncore.Muon
import io.muoncore.MuonBuilder
import io.muoncore.config.AutoConfiguration
import io.muoncore.config.MuonConfigBuilder
import io.muoncore.protocol.event.ClientEvent
import io.muoncore.protocol.event.Event
import io.muoncore.protocol.event.client.DefaultEventClient
import io.muoncore.protocol.event.client.EventReplayMode
import reactor.rx.broadcast.Broadcaster

import static io.muoncore.protocol.reactivestream.server.PublisherLookup.PublisherType.HOT
import static io.muoncore.protocol.requestresponse.server.HandlerPredicates.path

class ScanList {

    static void main(String[] args) throws Exception {

        Scans scans = new Scans()
        scans.init()

        AutoConfiguration config = MuonConfigBuilder
                .withServiceIdentifier("scan-list")
                .addWriter({ AutoConfiguration autoConfiguration ->
            if (System.getenv().MUON_URL) {
                autoConfiguration.properties["amqp.transport.url"] = System.getenv().MUON_URL
                autoConfiguration.properties["amqp.discovery.url"] = System.getenv().MUON_URL
            }
        }).build();

        Muon muon = MuonBuilder.withConfig(config).build();
        def ev = new DefaultEventClient(muon)
        muon.getDiscovery().blockUntilReady();

        muon.handleRequest(path("/anomaly-not-expected")) {
            ClientEvent event = ClientEvent.ofType("AnomalyNotExpected")
                    .schema("http://www.simplicityitself.io/corgi/anomalyDiscarded/1")
                    .stream("anomaly-training")
                    .payload(it.request.getPayload(Map)
            ).build()

            it.ok(ev.event(event))
        }

        muon.handleRequest(path("/anomaly-expected")) {
            ClientEvent event = ClientEvent.ofType("AnomalyExpected")
                    .schema("http://www.simplicityitself.io/corgi/anomalyAccepted/1")
                    .stream("anomaly-training")
                    .payload(it.request.getPayload(Map) ).build()

            it.ok(ev.event(event))
        }

        muon.handleRequest(path("/")) {
            println "Returning user information for ${it.request.getPayload(Map)}"
            it.ok(scans.getScanFor("david"))
        }

        muon.publishGeneratedSource("/", HOT) {
            println "Generating stream for $it.args"
            scans.getScanStreamFor("david")
        }

        Broadcaster<Event<Map>> b = Broadcaster.create()

        b.consume {
            try {
                println "PRECESSING $it.payload"
                scans.process(it)
            } catch (e) {
                e.printStackTrace()
            }
        }

        ev.replay("anomaly-training", EventReplayMode.REPLAY_THEN_LIVE, Map.class, b)
    }
}
