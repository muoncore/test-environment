package io.corgi

import io.muoncore.protocol.event.Event
import org.reactivestreams.Publisher
import reactor.rx.broadcast.Broadcaster

class Scans {

    Broadcaster pub = Broadcaster.create()

    def scans = Collections.synchronizedMap([
            site: "www.simplicityitself.io",
            scans: Collections.synchronizedList([
                    [
                            id: UUID.randomUUID(),
                            date: new Date(),
                            anomalies: []
                    ],
                    [
                            id: UUID.randomUUID(),
                            date: new Date(),
                            anomalies: [
                                    anomaly("Your SSL Certificate is expired"),
                                    anomaly("The site is responding slowly"),
                                    anomaly("The front page has changed significantly")
                            ]
                    ],
                    [
                            id: UUID.randomUUID(),
                            date: new Date(),
                            anomalies: [
                                    anomaly("HTTPs returns the wrong type of certificate"),
                                    anomaly("The site cannot be contacted!")
                            ]
                    ]
            ])
    ])

    def init() {
        Thread.start {
            try {
                while (true) {
                    println "UPDATING SCAN"
                    scans.scans.add(0, [
                            id       : UUID.randomUUID().toString(),
                            date     : new Date(),
                            anomalies: [
                                    anomaly("HTTPs returns the wrong type of certificate"),
                                    anomaly("The site cannot be contacted!")
                            ]
                    ])
                    if (scans.scans.size() > 10) scans.scans = scans.scans[0..10]
                    try {
                        pub.accept(scans)
                    } catch(Exception e) {}
                    sleep(60000)
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
        }
    }

    def process(Event ev) {
        def scanId = ev.payload.scanId
        def anomalyId = ev.payload.id

        def anoms = scans.scans.anomalies
        anoms = anoms.flatten()
        println "Anoms=$anoms"

        def anomaly = anoms.find {
            it.id ==  anomalyId
        }
        println "FOUND=$anomaly"

        if (!anomaly) {
            println "Can't find anomaly ... $anomalyId"
            return
        }
        println "Found $anomaly"
        anomaly.trained = true
        try {
            pub.accept(scans)
        } catch (e) {}
    }

    def getScanFor(String user) {
        scans
    }
    Publisher getScanStreamFor(String user) {
        pub
    }

    private def anomaly(String text) {
        [
                id: UUID.randomUUID().toString(),
                description: text,
                trained: false
        ]
    }
}
