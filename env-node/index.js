
var process = require("process")
var Muon = require("muon-core")
var muonurl = process.env.MUON_URL || "amqp://muon:microservices@rabbitmq"

logger.info("Muon is enabled, booting up using url " + muonurl)
var name = "env-node"
var muon = Muon.create(name, muonurl, muonurl, ["node", "test-environment"]);


muon.handle('/string-response', function (event, respond) {
  respond("pong")
});

muon.handle('/object-response', function (event, respond) {
  respond({"message": "pong"})
});

muon.handle('/start-event-hammer', function (event, respond) {
  respond({"message": "pong"})
});

muon.handle('/stop-event-hammer', function (event, respond) {
  respond({"message": "pong"})
});

var running = false

var ratePersecond = 100

function eventHammer() {
  if (running) return

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

muon.handle('/start-event-hammer', function (event, respond) {
  eventHammer()
  respond("Started event hammer")
});

muon.handle('/stop-event-hammer', function (event, respond) {
  running = false
  respond("Stopped event hammer")
});
