package envjvm

import groovy.json.JsonSlurper
import io.muoncore.Muon
import io.muoncore.MuonBuilder
import io.muoncore.config.AutoConfiguration
import io.muoncore.config.MuonConfigBuilder
import io.muoncore.protocol.event.Event
import io.muoncore.protocol.event.client.DefaultEventClient
import reactor.rx.broadcast.Broadcaster

import static io.muoncore.protocol.reactivestream.server.PublisherLookup.PublisherType.HOT
import static io.muoncore.protocol.requestresponse.server.HandlerPredicates.path

class EnvJvm {

    static void main(String[] args) throws Exception {

        AutoConfiguration config = MuonConfigBuilder
                .withServiceIdentifier("env-jvm")
                .addWriter({ AutoConfiguration autoConfiguration ->
            if (System.getenv().MUON_URL) {
                autoConfiguration.properties["amqp.transport.url"] = System.getenv().MUON_URL
                autoConfiguration.properties["amqp.discovery.url"] = System.getenv().MUON_URL
            }
        }).build();

        Muon muon = MuonBuilder.withConfig(config).build();
        def ev = new DefaultEventClient(muon)
        muon.getDiscovery().blockUntilReady();

        //// Functional
        // rpc string
        muon.handleRequest(path("/string-response")) {
            it.ok("pong")
        }

        // rpc object
        muon.handleRequest(path("/object-response")) {
            it.ok([
                    message: "pong"
            ])
        }

        // stream string
        muon.publishSource("strings", HOT, stringStream())

        // stream object
        muon.publishSource("objects", HOT, objectStream())

        //// Non functional

        // stream fast as possible
        muon.publishSource("fast-as-possible", HOT, fastStream())

        // stream big objects every second
        muon.publishSource("large-payload", HOT, largeobjectStream())


    }

    static stringStream() {
        Broadcaster<Event<Map>> b = Broadcaster.create()

        Thread.start {
            while(true) {
                always { b.accept("HELLO WORLD") }
                sleep(1000)
            }
        }
        b
    }

    static objectStream() {
        Broadcaster<Event<Map>> b = Broadcaster.create()

        Thread.start {
            while(true) {
                always { b.accept([message: "HELLO WORLD"]) }
                sleep(1000)
            }
        }

        b
    }

    static fastStream() {
        Broadcaster<Event<Map>> b = Broadcaster.create()

        Thread.start {
            while(true) {
                always { b.accept([message: "HELLO WORLD"]) }
            }
        }

        b
    }

    static largeobjectStream() {
        Broadcaster<Event<Map>> b = Broadcaster.create()

        Thread.start {
            while(true) {
                always { b.accept(LARGE_VALUE) }
                sleep(1000)
            }
        }

        b
    }

    static def always(exec) {
        try {
            exec()
        } catch (e) {}
    }

    static def LARGE_VALUE = new JsonSlurper().parseText("""
[
  {
    "_id": "587763589dc5c316592c4560",
    "index": 0,
    "guid": "fe15e5bc-56c4-453b-a26b-4fba49b0d67a",
    "isActive": true,
    "balance": "\$2,630.28",
    "picture": "http://placehold.it/32x32",
    "age": 40,
    "eyeColor": "brown",
    "name": {
      "first": "Elsie",
      "last": "Benton"
    },
    "company": "QUINEX",
    "email": "elsie.benton@quinex.co.uk",
    "phone": "+1 (882) 431-2371",
    "address": "543 Lott Place, Coalmont, Indiana, 3185",
    "about": "Consequat culpa aute est commodo magna consectetur laboris ullamco. Velit sit proident laboris Lorem irure irure exercitation est. Minim in proident aute elit velit aute veniam proident consectetur consequat laborum ea mollit magna. Proident id ut labore ex aliquip mollit ad.",
    "registered": "Friday, May 23, 2014 9:46 AM",
    "latitude": "27.154849",
    "longitude": "175.845832",
    "tags": [
      "aute",
      "deserunt",
      "adipisicing",
      "irure",
      "consectetur"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Mcmahon Campbell"
      },
      {
        "id": 1,
        "name": "Barbra Garner"
      },
      {
        "id": 2,
        "name": "Reid Moody"
      }
    ],
    "greeting": "Hello, Elsie! You have 8 unread messages.",
    "favoriteFruit": "apple"
  },
  {
    "_id": "587763589b9dad5d205135e6",
    "index": 1,
    "guid": "270b3e9b-4449-4ae4-9419-f0a66aed5eb6",
    "isActive": true,
    "balance": "\$1,379.85",
    "picture": "http://placehold.it/32x32",
    "age": 32,
    "eyeColor": "brown",
    "name": {
      "first": "Mcneil",
      "last": "Fisher"
    },
    "company": "ZILLAR",
    "email": "mcneil.fisher@zillar.me",
    "phone": "+1 (841) 429-2385",
    "address": "185 Louise Terrace, Allison, California, 7350",
    "about": "Laborum consectetur irure veniam duis excepteur velit qui commodo fugiat officia cupidatat enim cillum. Est sint ut laboris ea officia elit ut qui. Et minim dolore fugiat minim ullamco excepteur mollit aliqua. Dolor nisi anim consectetur amet proident minim. Consectetur nulla reprehenderit esse occaecat minim.",
    "registered": "Wednesday, September 28, 2016 12:13 AM",
    "latitude": "16.634024",
    "longitude": "-68.663069",
    "tags": [
      "esse",
      "magna",
      "consequat",
      "aute",
      "aliqua"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Franklin Rivas"
      },
      {
        "id": 1,
        "name": "Le Figueroa"
      },
      {
        "id": 2,
        "name": "Lorie Myers"
      }
    ],
    "greeting": "Hello, Mcneil! You have 6 unread messages.",
    "favoriteFruit": "apple"
  },
  {
    "_id": "587763580b6aadc6ef9a0fa7",
    "index": 2,
    "guid": "9c872186-8bc1-46fe-ab66-f8b51ab33d32",
    "isActive": false,
    "balance": "\$3,541.52",
    "picture": "http://placehold.it/32x32",
    "age": 27,
    "eyeColor": "blue",
    "name": {
      "first": "Gilmore",
      "last": "Nieves"
    },
    "company": "EVENTIX",
    "email": "gilmore.nieves@eventix.ca",
    "phone": "+1 (967) 518-2639",
    "address": "174 Prospect Street, Osage, Kansas, 3026",
    "about": "Deserunt ullamco incididunt ad dolore ullamco ipsum nulla labore aliqua magna. Do incididunt officia est consequat exercitation exercitation fugiat qui laborum ullamco reprehenderit duis. Duis ullamco non cillum sunt qui laboris do incididunt exercitation ipsum dolore sit adipisicing esse. Dolore irure culpa elit eiusmod quis Lorem mollit amet duis pariatur est quis ut culpa.",
    "registered": "Tuesday, August 25, 2015 12:01 AM",
    "latitude": "-13.863987",
    "longitude": "-126.538129",
    "tags": [
      "magna",
      "in",
      "magna",
      "non",
      "sint"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Stanton Wilkinson"
      },
      {
        "id": 1,
        "name": "Stacy Parker"
      },
      {
        "id": 2,
        "name": "Jimenez West"
      }
    ],
    "greeting": "Hello, Gilmore! You have 5 unread messages.",
    "favoriteFruit": "apple"
  },
  {
    "_id": "5877635849e522bdfb4b71b3",
    "index": 3,
    "guid": "13bc3223-b767-4884-bb9a-9d69659af25b",
    "isActive": false,
    "balance": "\$3,360.14",
    "picture": "http://placehold.it/32x32",
    "age": 27,
    "eyeColor": "green",
    "name": {
      "first": "Adeline",
      "last": "Cochran"
    },
    "company": "ZOLAR",
    "email": "adeline.cochran@zolar.info",
    "phone": "+1 (956) 422-2116",
    "address": "206 Grove Place, Harviell, American Samoa, 2253",
    "about": "Ut aliquip adipisicing sunt incididunt id laborum esse eu aute voluptate pariatur proident. Deserunt fugiat esse tempor officia enim laboris. Ut adipisicing eu fugiat veniam ea id. Et id aute nostrud eiusmod Lorem ad minim irure nostrud officia nulla adipisicing. Ipsum veniam deserunt ullamco ullamco adipisicing sint ea esse reprehenderit in. Exercitation irure duis consequat eiusmod velit duis ex mollit elit cupidatat ullamco cillum nulla quis.",
    "registered": "Saturday, May 10, 2014 5:32 PM",
    "latitude": "47.440563",
    "longitude": "-43.834753",
    "tags": [
      "non",
      "mollit",
      "est",
      "cupidatat",
      "in"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Sears Colon"
      },
      {
        "id": 1,
        "name": "Catherine Daugherty"
      },
      {
        "id": 2,
        "name": "Cotton Cline"
      }
    ],
    "greeting": "Hello, Adeline! You have 5 unread messages.",
    "favoriteFruit": "banana"
  },
  {
    "_id": "58776358bc64f411ed4e2d64",
    "index": 4,
    "guid": "ad1a42f5-a472-4407-887a-4754f9162e54",
    "isActive": true,
    "balance": "\$3,396.80",
    "picture": "http://placehold.it/32x32",
    "age": 39,
    "eyeColor": "blue",
    "name": {
      "first": "Virginia",
      "last": "Blevins"
    },
    "company": "CAPSCREEN",
    "email": "virginia.blevins@capscreen.tv",
    "phone": "+1 (844) 419-3115",
    "address": "221 Bragg Court, Nicholson, Arkansas, 3402",
    "about": "Anim enim non ea voluptate. Aliqua ad tempor quis cupidatat pariatur do. Lorem eiusmod occaecat proident nulla Lorem consequat duis sint. Qui deserunt ea non aliqua sunt dolore ex exercitation ut. Id non anim consectetur exercitation sint laborum consectetur laboris labore sunt nisi anim ea id. Lorem nulla consectetur nostrud sint anim officia ex officia.",
    "registered": "Saturday, June 18, 2016 2:56 AM",
    "latitude": "16.858822",
    "longitude": "-127.014832",
    "tags": [
      "ex",
      "mollit",
      "duis",
      "anim",
      "excepteur"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Snider Walton"
      },
      {
        "id": 1,
        "name": "Nunez Parsons"
      },
      {
        "id": 2,
        "name": "Lynne Hahn"
      }
    ],
    "greeting": "Hello, Virginia! You have 8 unread messages.",
    "favoriteFruit": "banana"
  },
  {
    "_id": "587763581269b933a00c65fb",
    "index": 5,
    "guid": "bc441ac6-b4cc-4a68-ad0e-5c27bffe6043",
    "isActive": true,
    "balance": "\$2,520.60",
    "picture": "http://placehold.it/32x32",
    "age": 23,
    "eyeColor": "brown",
    "name": {
      "first": "Darlene",
      "last": "Burgess"
    },
    "company": "ELEMANTRA",
    "email": "darlene.burgess@elemantra.net",
    "phone": "+1 (974) 599-2381",
    "address": "703 Brown Street, Wauhillau, Pennsylvania, 5961",
    "about": "Et dolore excepteur ut occaecat dolore laborum magna ut occaecat ad nisi adipisicing pariatur. Excepteur laboris adipisicing laboris velit ullamco excepteur ut nisi nostrud qui. Esse exercitation aliqua id ullamco est pariatur est tempor ullamco minim Lorem tempor. Et sit Lorem Lorem in aliquip.",
    "registered": "Monday, June 9, 2014 1:36 AM",
    "latitude": "15.011283",
    "longitude": "-67.5349",
    "tags": [
      "incididunt",
      "adipisicing",
      "reprehenderit",
      "amet",
      "et"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Lucinda Curtis"
      },
      {
        "id": 1,
        "name": "Bradley Preston"
      },
      {
        "id": 2,
        "name": "Joy Howell"
      }
    ],
    "greeting": "Hello, Darlene! You have 5 unread messages.",
    "favoriteFruit": "banana"
  },
  {
    "_id": "587763583edb6711d6c17e22",
    "index": 6,
    "guid": "df3f6453-bbb7-412a-8109-611fd6707fd6",
    "isActive": false,
    "balance": "\$2,909.10",
    "picture": "http://placehold.it/32x32",
    "age": 23,
    "eyeColor": "brown",
    "name": {
      "first": "Denise",
      "last": "Singleton"
    },
    "company": "COMTRACT",
    "email": "denise.singleton@comtract.name",
    "phone": "+1 (925) 403-3892",
    "address": "349 Bushwick Avenue, Canby, Marshall Islands, 9175",
    "about": "Consequat cupidatat nostrud excepteur occaecat proident commodo anim minim aute elit non do cupidatat consequat. Ipsum anim Lorem amet dolore irure nisi. Duis sit laborum laboris ullamco dolor sint voluptate exercitation velit culpa Lorem sunt eu. Exercitation sint adipisicing eu ullamco reprehenderit incididunt reprehenderit do amet occaecat. Sit reprehenderit elit aute aliqua id reprehenderit ea quis sint ullamco minim. Aute cillum sint proident aliqua Lorem fugiat anim.",
    "registered": "Sunday, August 7, 2016 5:58 PM",
    "latitude": "-3.420415",
    "longitude": "-63.147549",
    "tags": [
      "duis",
      "ad",
      "tempor",
      "cupidatat",
      "dolore"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Craft Kim"
      },
      {
        "id": 1,
        "name": "Wilson Dixon"
      },
      {
        "id": 2,
        "name": "Concetta Huff"
      }
    ],
    "greeting": "Hello, Denise! You have 6 unread messages.",
    "favoriteFruit": "apple"
  },
  {
    "_id": "58776358c39d669b65fe78a0",
    "index": 7,
    "guid": "d49c34c0-bf4b-46e0-af70-20bb8080b758",
    "isActive": false,
    "balance": "\$2,120.38",
    "picture": "http://placehold.it/32x32",
    "age": 26,
    "eyeColor": "green",
    "name": {
      "first": "Nettie",
      "last": "Leblanc"
    },
    "company": "CABLAM",
    "email": "nettie.leblanc@cablam.com",
    "phone": "+1 (909) 467-2480",
    "address": "638 Oriental Boulevard, Efland, Federated States Of Micronesia, 9332",
    "about": "Ad duis tempor aute aliqua ut qui non. Veniam aute commodo nulla laboris irure esse id enim officia fugiat. In reprehenderit laboris culpa magna ea exercitation esse non culpa. Aliquip consectetur nostrud magna laborum voluptate eiusmod commodo qui nisi labore duis Lorem. Amet nulla quis et in nulla. Sit enim quis exercitation irure cupidatat velit pariatur deserunt qui. Ad nisi reprehenderit eu magna laborum nisi et mollit proident.",
    "registered": "Sunday, July 3, 2016 12:03 AM",
    "latitude": "84.390631",
    "longitude": "33.475105",
    "tags": [
      "magna",
      "eu",
      "tempor",
      "occaecat",
      "exercitation"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Simon Cantu"
      },
      {
        "id": 1,
        "name": "Roach Jimenez"
      },
      {
        "id": 2,
        "name": "Wheeler Camacho"
      }
    ],
    "greeting": "Hello, Nettie! You have 5 unread messages.",
    "favoriteFruit": "strawberry"
  },
  {
    "_id": "58776358e4e58638044cbe88",
    "index": 8,
    "guid": "d2815b91-30b2-4267-8173-305331aecd4c",
    "isActive": false,
    "balance": "\$2,278.33",
    "picture": "http://placehold.it/32x32",
    "age": 25,
    "eyeColor": "blue",
    "name": {
      "first": "Townsend",
      "last": "Owen"
    },
    "company": "CONCILITY",
    "email": "townsend.owen@concility.biz",
    "phone": "+1 (893) 497-3034",
    "address": "614 Beayer Place, Eastvale, Virgin Islands, 2101",
    "about": "Ad et voluptate quis nostrud deserunt consequat occaecat eiusmod elit exercitation non reprehenderit. Eiusmod adipisicing irure officia magna adipisicing nostrud labore laboris. Aliqua ex Lorem sint irure incididunt. Nostrud pariatur consequat ipsum in et velit minim aliqua laborum culpa fugiat dolor reprehenderit. Consequat ea consectetur Lorem deserunt tempor duis.",
    "registered": "Friday, June 5, 2015 5:41 PM",
    "latitude": "-83.423159",
    "longitude": "48.747509",
    "tags": [
      "proident",
      "id",
      "consectetur",
      "irure",
      "aliquip"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Noreen Brewer"
      },
      {
        "id": 1,
        "name": "Pratt Schwartz"
      },
      {
        "id": 2,
        "name": "Levy Ayers"
      }
    ],
    "greeting": "Hello, Townsend! You have 8 unread messages.",
    "favoriteFruit": "strawberry"
  },
  {
    "_id": "58776358f257693efa601218",
    "index": 9,
    "guid": "4c7cbbc4-f535-4a26-952e-70448c34bc21",
    "isActive": false,
    "balance": "\$2,534.34",
    "picture": "http://placehold.it/32x32",
    "age": 35,
    "eyeColor": "green",
    "name": {
      "first": "Page",
      "last": "Morales"
    },
    "company": "NORALI",
    "email": "page.morales@norali.io",
    "phone": "+1 (953) 579-2131",
    "address": "332 Seaview Court, Cataract, North Dakota, 9304",
    "about": "Id culpa commodo anim est. Officia minim ad veniam minim Lorem elit cillum pariatur laborum deserunt proident labore ex consectetur. Cupidatat Lorem eu qui sint reprehenderit magna aliqua enim cillum mollit duis. Culpa laborum ut tempor aliqua dolore tempor ut aliquip tempor commodo fugiat.",
    "registered": "Friday, March 20, 2015 3:50 AM",
    "latitude": "-88.70141",
    "longitude": "77.917269",
    "tags": [
      "enim",
      "eiusmod",
      "reprehenderit",
      "anim",
      "ex"
    ],
    "range": [
      0,
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9
    ],
    "friends": [
      {
        "id": 0,
        "name": "Maryann Whitfield"
      },
      {
        "id": 1,
        "name": "Blanche Wyatt"
      },
      {
        "id": 2,
        "name": "Vicky Peck"
      }
    ],
    "greeting": "Hello, Page! You have 10 unread messages.",
    "favoriteFruit": "strawberry"
  }
]


""")
}
