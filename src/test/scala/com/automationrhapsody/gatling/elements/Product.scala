package com.automationrhapsody.gatling.elements

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.util.Random

object Product {

  private val reqGoToHome = exec(http("Open home page")
    .get("/products")
    // Check for this string, if not found result will be KO and test will fail
    .check(regex("Search: "))
    // Optional means if not found result still be OK
    .check(regex("NotFound").optional)
  )

  private val reqSearchProduct = exec(http("Search product")
    // search_term is value on first line in search_terms.csv file read by CSV feeder
    .get("/products?q=${search_term}&action=search-results")
    // Check same search_term is output, capture results number in numberOfProducts session attribute
    .check(regex("Your search for '${search_term}' gave ([\\d]{1,2}) results:").saveAs("numberOfProducts"))
  )

  private val reqOpenProduct = exec(session => {
    // Get numberOfProducts from Session as String and then convert to Int.
    // Note: toString will not work as this is SessionAttribute object and it has toString() method
    var numberOfProducts = session("numberOfProducts").as[String].toInt
    // Get random productId from 1 to numberOfProducts inclusive
    var productId = Random.nextInt(numberOfProducts) + 1
    // Set productId in session. Session is immutable so new session object is created
    session.set("productId", productId)
    // In current function ${productId} is not in session, so cannot be used
  }).exec(http("Open Product")
    // ${productId} is now in session and can be used
    .get("/products?action=details&id=${productId}")
    // Check correct productId is output
    .check(regex("This is 'Product ${productId} name' details page."))
  )

  // Create CSV feeder from search_terms.csv which is iterated and items are accessed in random order
  // If circular is not used once file is being read execution will stop with error
  private val csvFeeder = csv("search_terms.csv").circular.random

  val scnSearch = Constants.createScenario("Search", csvFeeder,
    reqGoToHome, reqSearchProduct, reqGoToHome)

  val scnSearchAndOpen = Constants.createScenario("Search and Open", csvFeeder,
    reqGoToHome, reqSearchProduct, reqOpenProduct, reqGoToHome)
}
