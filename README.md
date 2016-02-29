# sample-performance-with-gatling #
This project is intended to demonstrate how to use Gatling performance testing tool.
More details can be found in the original blog post <a href="http://automationrhapsody.com/performance-testing-with-gatling-integration-with-maven/">Performance testing with Gatling - integration with Maven</a>.

## Run ##
1. Run RESTful server used for testing. See more details in <a href="http://automationrhapsody.com/build-a-rest-stub-server-with-dropwizard/">Build a RESTful stub server with Dropwizard</a> blog post.
2. Run one of the simulations with one of the commands ("package" is used in mvn command to copy all resources to "target" folder):

	`mvn package gatling:execute -Dgatling.simulationClass=com.automationrhapsody.gatling.simulations.ProductSimulation`

	`mvn package gatling:execute -Dgatling.simulationClass=com.automationrhapsody.gatling.simulations.PersonSimulation` 
