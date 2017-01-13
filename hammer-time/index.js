
var process = require("process")
var Muon = require("muon-core")
var muonurl = process.env.MUON_URL || "amqp://muon:microservices@rabbitmq"

logger.info("Muon is enabled, booting up using url " + muonurl)
var name = "hammer-time"
var muon = Muon.create(name, muonurl, muonurl, ["node", "test-environment"]);

var running = false

function subscribe() {
  muon.replay("hammer",
    {"stream-type": "hot"},
    function(data) {
      logger.error("Data...")
      console.dir(data)
    },
    function(error) {
      logger.error("Errored...")
      console.dir(error)
    },
    function() {
      logger.warn("COMPLETED STREAM")
    }
  )

}

function eventHammer() {
  if (running) return

  var ratePersecond = 10

  running = true
  var idx = 0
  function hammer() {
    for (var i = 0; i < ratePersecond; i++) {
      console.log("HAMMERING " + idx++)

      muon.emit({
        "event-type": "BadJuJu"+idx,
        "schema": "http://www.simplicityitself.io/corgi/siteCheck/1",
        "stream-name": "hammer",
        "service-id": "env-node",
        payload: {
          "hammer": "time"
        }
      }).then(function (resp) {
        console.dir(resp)
      }).catch(function(error) {
        console.dir(error)
      })
    }

    if (running) setTimeout(hammer, 1000)
  }

  hammer()
}

//wait to settle so we don't overload muon node CSP channels
setTimeout(function() {
  subscribe()
  eventHammer()
}, 6000)
