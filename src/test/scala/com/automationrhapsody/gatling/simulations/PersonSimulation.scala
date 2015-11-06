package com.automationrhapsody.gatling.simulations

import com.automationrhapsody.gatling.elements.{Constants, Person}
import io.gatling.core.Predef._

class PersonSimulation extends Simulation {

  setUp(
    Person.scnGet.inject(atOnceUsers(Constants.numberOfUsers)),
    Person.scnSaveAndGet.inject(atOnceUsers(Constants.numberOfUsers))
  )
    .protocols(Constants.httpProtocol)
    .pauses(constantPauses)
    .maxDuration(Constants.duration)
    .assertions(
      global.responseTime.max.lessThan(Constants.responseTimeMs),
      global.successfulRequests.percent.greaterThan(Constants.responseSuccessPercentage)
    )
}