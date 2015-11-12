package com.automationrhapsody.gatling.simulations

import com.automationrhapsody.gatling.elements.{Constants, Product}
import io.gatling.core.Predef._

import scala.concurrent.duration._

class ProductSimulation extends Simulation {

  setUp(
    // Ramp up all user for 10 seconds, import scala.concurrent.duration._ is needed
    Product.scnSearch.inject(rampUsers(Constants.numberOfUsers) over 10.seconds),
    // Ramp up all at once
    Product.scnSearchAndOpen.inject(atOnceUsers(Constants.numberOfUsers))
  )
    // HTTP protocol with more specific configurations can be defined in each simulation
    // In this case default protocol is being updated to include all HTML resources (JS, CSS, images)
    .protocols(Constants.httpProtocol.inferHtmlResources())
    // Pauses between requests are always one and the same
    .pauses(constantPauses)
    // Max run duration
    .maxDuration(Constants.duration)
    .assertions(
      // Response time should be less that predefined value
      global.responseTime.max.lessThan(Constants.responseTimeMs),
      // Percentage of success responses should be greater than predefined value
      global.successfulRequests.percent.greaterThan(Constants.responseSuccessPercentage)
    )
    // Throttling ensures required req/s will be accomplished. Scenario should run forever, numberOfRepetitions=-1
    .throttle(reachRps(100) in 10.seconds)
}