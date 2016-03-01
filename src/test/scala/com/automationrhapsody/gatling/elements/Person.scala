package com.automationrhapsody.gatling.elements

// import io.gatling.core.Predef._ is mandatory as there are tools used to convert data types

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.io.Source
import scala.util.Random

object Person {

  private val added = "Added"
  private val updated = "Updated"

  private val reqGetAll = exec(http("Get All Persons")
    .get("/person/all")
    // Check that there are more than one occurrence of current regex and save as 'count' session attribute
    .check(regex("\"firstName\":\"(.*?)\"").count.greaterThan(1).saveAs("count"))
    // Check that there is exactly one array bracket
    .check(regex("\\[").count.is(1))
    // Find all occurences of 'id' and capture this into person_ids List in session
    .check(regex("\"id\":([\\d]{1,6})").findAll.saveAs("person_ids"))
    // person_ids List cannot be used at this time in session
  ).exec(session => {
    // In this function person_ids List is now accessible from session
    // Get count, it is Int now because was stored in session on previous function
    val count = session("count").as[Int]
    // Get person_ids and convert to List of Int
    val personIds = session("person_ids").as[List[Int]]
    // Get random personId from personIds List, count of firstNames should be the same as count of ids
    // Explicit conversion toString is needed and then get Int value with toInt
    val personId = personIds(Random.nextInt(count)).toString.toInt
    // Set personId in session and return it
    // Note: if you return 'session' old object will be returned and new attribute will be missing
    session.set("person_id", personId)
  }).exec(session => {
    // Debug session to verify in tests output all values are correct
    println(session)
    session
  })

  private val reqGetPerson = exec(http("Get Person")
    // ${person_id} should be in session otherwise error will show "No attribute named 'person_id' is defined"
    .get("/person/get/${person_id}")
    // Check only one firstName is shown
    .check(regex("\"firstName\":\"(.*?)\"").count.is(1))
    // Check result is not JSON array
    .check(regex("\\[").notExists)
  )

  private val reqSavePerson = exec(http("Save Person")
    // Make post request
    .post("/person/save")
    // POST body is read from file with variables later replaced with session data by Gatling EL engine
    .body(ElFileBody("person.json"))
    // Content-Type is needed otherwise REST service fails
    .header(HttpHeaderNames.ContentType, HttpHeaderValues.ApplicationJson)
    // Check result id and save it to session
    .check(regex("Person with id=([\\d]{1,6})").saveAs("person_id"))
    .check(regex("\\[").notExists)
    // Check action is Added or Updated and save it in session
    .check(regex("(" + added + "|" + updated + ") Person with id=").saveAs("action"))
  )

  private val reqGetPersonAferSave = exec(http("Get Person After Save")
    .get("/person/get/${person_id}")
    // Check name data in person JSON is same as the one in session used for save
    .check(regex("\"id\":${person_id}"))
    .check(regex("\"firstName\":\"${first_name}\""))
    .check(regex("\"lastName\":\"${last_name}\""))
    .check(regex("\"email\":\"${email}\""))
  )

  private val reqGetPersonAferUpdate = exec(http("Get Person After Update")
    .get("/person/get/${person_id}")
    // Check only id, as other data is different
    .check(regex("\"id\":${person_id}"))
  )

  private val uniqueIds: List[String] = Source
    // Read text file
    .fromInputStream(getClass.getResourceAsStream("/account_ids.txt"))
    // Each line is added as new entry to a List
    .getLines().toList

  // Create custom feeder with important data and dummy data
  private val feedSearchTerms = Iterator.continually(buildFeeder(uniqueIds))

  /**
   * Creates Map taking list of important data and filling other variables with dummy data.
   * This increases the number of combinations.
   * @return Feeder Map
   */
  private def buildFeeder(dataList: List[String]): Map[String, Any] = {
    Map(
      "id" -> (Random.nextInt(100) + 1),
      // Get 5 alphanumeric characters and convert to String with mkString
      // Note: toString will not work as Stream has toString() method
      "first_name" -> Random.alphanumeric.take(5).mkString,
      "last_name" -> Random.alphanumeric.take(5).mkString,
      // Get 5 alphanumeric character, convert to String and concatenate email suffix at the end
      "email" -> Random.alphanumeric.take(5).mkString.concat("@na.na"),
      // Get random unique ID, this allows creation of feeder with important data mixed with dummy data
      "unique_id" -> dataList(Random.nextInt(dataList.size))
    )
  }

  val scnGet = Constants.createScenario("Get all then one", feedSearchTerms, reqGetAll, reqGetPerson)

  val scnSaveAndGet = Constants.createScenario("Save and get", feedSearchTerms, reqSavePerson)
    .doIfEqualsOrElse("${action}", added) {
      // If this is Save Person then execute reqGetPersonAferSave
      reqGetPersonAferSave
    } {
      // If this is Update Person then execute reqGetPersonAferUpdate
      reqGetPersonAferUpdate
    }
}
