package com.automationrhapsody.gatling.simulations.original

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ProductSimulation extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:9000")
    .inferHtmlResources()


  val uri1 = "http://localhost:9000/products"

  val scn = scenario("RecordedSimulation")
    .exec(http("request_0")
      .get("/products"))
    .pause(11)
    .exec(http("request_1")
      .get("/products?q=SearchString&action=search-results"))
    .pause(8)
    .exec(http("request_2")
      .get("/products?action=details&id=1"))
    .pause(6)
    .exec(http("request_3")
      .get("/products"))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}