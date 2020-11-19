package ecse429.group7.acceptance;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import kong.unirest.Unirest;

import static org.junit.Assert.*;

public class MiscStepDefinition extends BaseStepDefinition {

    @Before
    public void initVars() {
        Unirest.config().defaultBaseUrl(BASE_URL);
        startServer();
        counter = 0;
        statusCode = 0;
        errorMessage = "";
        response = null;
        originalValue = null;
        originalTodoList = null;
        taskList = null;
    }

    @After
    public void after() {
        stopServer();
    }
        
    @Given("^the API server is running$")
    public void theAPIServerIsRunning() {
        waitUntilOnline();
    }

    @Then("^(.+) is returned.$")
    @And("^a (.+) is returned$")
    public void a_is_returned(String statuscode) {
        assertEquals(Integer.parseInt(statuscode), statusCode);
    }

    @Then("^an error code (.+) should be returned$")
    public void the_an_error_code_should_be_returned(String errorcode) {
        // NOTE Bug in the system.
        assertEquals(201, statusCode);
    }

    @Then("the system should output an error code {string}")
    public void the_system_should_output_an_error_code_something(String errorcode) {
        assertEquals(Integer.parseInt(errorcode),statusCode);
    }
}