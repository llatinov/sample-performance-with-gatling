package com.automationrhapsody.gatling.elements

import io.gatling.core.Predef._
import io.gatling.core.feeder.FeederBuilder
import io.gatling.core.result.message.Status
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.request.ExtraInfo

import scala.concurrent.duration._

object Constants {
  val numberOfUsers: Int = System.getProperty("numberOfUsers").toInt
  val duration: FiniteDuration = System.getProperty("durationMinutes").toInt.minutes
  val pause: FiniteDuration = System.getProperty("pauseBetweenRequestsMs").toInt.millisecond
  val responseTimeMs = 500
  val responseSuccessPercentage = 99
  private val url: String = System.getProperty("url")
  private val repeatTimes: Int = System.getProperty("numberOfRepetitions").toInt
  private val successStatus: Int = 200
  private val isDebug = System.getProperty("debug").toBoolean

  // Define HTTP protocol to be used in simulations
  val httpProtocol = http
    .baseURL(url)
    // Check response code is 200
    .check(status.is(successStatus))
    // Extract more info to ease debugging
    .extraInfoExtractor { extraInfo => List(getExtraInfo(extraInfo)) }

  /**
   * Creates a scenario by given, name, feed and executions.
   * @param name Scenario name
   * @param feed Feed used to put data into session
   * @param chains Executable that are chained together
   * @return
   */
  def createScenario(name: String, feed: FeederBuilder[_], chains: ChainBuilder*): ScenarioBuilder = {
    // Do given amount of repetitions only
    if (Constants.repeatTimes > 0) {
      scenario(name).feed(feed).repeat(Constants.repeatTimes) {
        exec(chains).pause(Constants.pause)
      }
    } else {
      // Loop forever, it is important to put maxDuration() in Simulation setUp() method
      scenario(name).feed(feed).forever() {
        exec(chains).pause(Constants.pause)
      }
    }
  }

  private def getExtraInfo(extraInfo: ExtraInfo): String = {
    // Dump request/response in case of error or in Debug mode
    if (isDebug
      || extraInfo.response.statusCode.get != successStatus
      || extraInfo.status.eq(Status.valueOf("KO"))) {
      ",URL:" + extraInfo.request.getUrl +
        " Request: " + extraInfo.request.getStringData +
        " Response: " + extraInfo.response.body.string
    } else {
      ""
    }
  }
}