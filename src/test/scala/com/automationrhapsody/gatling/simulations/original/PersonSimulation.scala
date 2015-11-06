package com.automationrhapsody.gatling.simulations.original

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class PersonSimulation extends Simulation {

  val httpProtocol = http
    .baseURL("http://localhost:9000")
    .inferHtmlResources()

  val headers_0 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_1 = Map(
    "Origin" -> "chrome-extension://fhbjgbiflinjbdggehcddcbncdddomop",
    "Postman-Token" -> "9577054e-c4a3-117f-74ab-e84a2be473e0")

  val headers_2 = Map(
    "Origin" -> "chrome-extension://fhbjgbiflinjbdggehcddcbncdddomop",
    "Postman-Token" -> "639b36ea-aff3-1b85-618e-c696734afc6e")

  val uri1 = "http://localhost:9000/person"

  val scn = scenario("RecordedSimulation")
    .exec(http("request_0")
      .get("/person/all")
      .headers(headers_0))
    .pause(9)
    .exec(http("request_1")
      .post("/person/save")
      .headers(headers_1)
      .body(RawFileBody("RecordedSimulation_0001_request.txt")))
    .pause(3)
    .exec(http("request_2")
      .post("/person/save")
      .headers(headers_2)
      .body(RawFileBody("RecordedSimulation_0002_request.txt")))

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}