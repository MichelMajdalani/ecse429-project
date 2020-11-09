package ecse429.group7;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.java.After;
import io.cucumber.java.Before;


import io.cucumber.datatable.DataTable;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONObject;

import static org.junit.Assert.*;

import java.util.List;

public class PriorityStepDefinition extends BaseTest {

    String errorMessage;
    JSONObject originalValue;
    JSONObject response;
    JSONObject originalTodoList;

    @Before
    public static void before() {
        Unirest.config().defaultBaseUrl(BASE_URL);
        startServer();
    }

    @Before
    public void initVars() {
        errorMessage = "";
        response = null;
        originalValue = null;
        originalTodoList = null;
    }

    @Before

    @After
    public static void after() {
        stopServer();
    }

    @Given("^the following categories are registered in the todoManagerRestAPI system:$")
    public void the_following_categories_are_registered_in_the_todo_manager_restapi_system(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
    
        boolean firstLine = true;
        for (List<String> columns : rows) {
            // ignore title row
            if(!firstLine) {
                Unirest.post("/categories")
                        .body("{\n\"description\":\"" + columns.get(1) + "\",\n  \"title\":\""
                                + columns.get(0) + "\"\n}")
                        .asJson();
            }
            firstLine = false;
        }
    }

    @Given("^the following todo is registered in the system:$")
    public void the_following_todo_is_registered_in_the_system(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
    
        boolean firstLine = true;
        for (List<String> columns : rows) {
            // ignore title row
            if(!firstLine) {
                Unirest.post("/todos")
                        .body("{\"title\":\"" + columns.get(0) + "\",\"doneStatus\":"
                                + columns.get(1) + ",\"description\":\"" + columns.get(2) + "\"}")
                        .asJson();
            }
            firstLine = false;
        }
    }

    @When("^user requests to categorize todo with title \"([^\"]*)\" as \"([^\"]*)\" priority$")
    public void user_requests_to_categorize_todo_with_title_something_as_something_priority(String todo_title, String priority) {
        // Find ID of Task todo_title
        int id = findIdFromTodoName(todo_title);
 
        HttpResponse<JsonNode> response = Unirest.post("/todos/" + id +"/categories")
                .body("{\n\"title\":\"" + priority + "\"\n}\n").asJson();

        if(response.getStatus() != 200 && response.getStatus() != 201) {
            errorMessage = response.getBody().getObject().getJSONArray("errorMessages").getString(0);
        }

    }

    @When("^user requests to remove \"([^\"]*)\" priority categorization from \"([^\"]*)\"$")
    public void user_requests_to_remove_something_priority_categorization_from_something(String priority, String task) {
        int category_id = findIdFromTodoCategoryName(priority, task);
        int todo_id = findIdFromTodoName(task);
        
        Unirest.delete("/todos/" + todo_id + "/categories/" + category_id)
                .header("Content-Type", "application/json")
        .asJson();
    }

    @When("^user requests to add \"([^\"]*)\" priority categorization to \"([^\"]*)\"$")
    public void user_requests_to_add_something_priority_categorization_to_something(String priority, String todo_title) {
        user_requests_to_categorize_todo_with_title_something_as_something_priority(todo_title, priority);
    }

    @Then("^the \"([^\"]*)\" should be classified as a \"([^\"]*)\" priority task$")
    public void the_something_should_be_classified_as_a_something_priority_task(String todo, String priority) {
        int category_id = findIdFromTodoCategoryName(priority, todo);

        // if != -1, then found category with name 
        assertTrue(category_id != -1);
    }

    @Then("^the system should output an error message$")
    public void the_system_should_output_an_error_message() {
        assertEquals(errorMessage, "Could not find parent thing for relationship todos/-1/categories");
    }

    @And("^the todo \"([^\"]*)\" is assigned as a \"([^\"]*)\" priority task$")
    public void the_todo_something_is_assigned_as_a_something_priority_task(String todo_title, String priority) {
        user_requests_to_categorize_todo_with_title_something_as_something_priority(todo_title, priority);
    }

    @Given("^the API server is running$")
    public void theAPIServerIsRunning() {
        waitUntilOnline();
    }

    @And("^the following todos registered in the system$")
    public void theFollowingTodosRegisteredInTheSystem(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);

        boolean firstLine = true;
        for (List<String> columns : rows) {
            // ignore title row
            if(!firstLine) {
                String title = "\"title\":\"" + columns.get(0) + "\"";
                String doneStatus = "\"doneStatus\":" + columns.get(1);
                String description = "\"description\":\"" + columns.get(2) + "\"";
                Unirest.post("/todos")
                        .body("{\n" + title + ",\n" + doneStatus + ",\n" + description + "\n}")
                        .asJson();
            }
            firstLine = false;
        }
    }

    public static void assertDoneStatusEquals(JSONObject todo, boolean val) {
        assertNotNull(todo);
        assertTrue(todo.getString("doneStatus").equalsIgnoreCase(val + ""));
    }

    @Given("^([^>]*) is the title of a todo registered on the system$")
    public void selectedTitleIsTheTitleOfATodoRegisteredOnTheSystem(String selectedTitle) {
        assertNotNull(findTodoByName(selectedTitle));
    }

    @Given("^([^>]*) is not a title of a todo registered on the system$")
    public void selectedTitleIsNotTheTitleOfATodoRegisteredOnTheSystem(String selectedTitle) {
        assertNull(findTodoByName(selectedTitle));
    }

    @And("^the todo with title ([^>]*) is not marked as done$")
    public void theTodoWithTitleSelectedTitleIsNotMarkedAsDone(String selectedTitle) {
        assertDoneStatusEquals(findTodoByName(selectedTitle), false);
    }

    @And("^the todo with title ([^>]*) is marked as done$")
    public void theTodoWithTitleSelectedTitleIsMarkedAsDone(String selectedTitle) {
        assertDoneStatusEquals(findTodoByName(selectedTitle), true);
    }

    @When("^the user chooses to mark the task named ([^>]*) as done$")
    public void theTheUserChoosesToMarkTheTaskNamedSelectedTitleAsDone(String selectedTitle) {
        originalTodoList = Unirest.get("/todos").asJson().getBody().getObject();
        JSONObject todo = findTodoByName(selectedTitle);
        if (todo == null) {
            response = Unirest.post("/todos/-1").asJson().getBody().getObject();
            return;
        }
        originalValue = new JSONObject(todo.toString());
        int id = todo.getInt("id");
        todo.remove("id");
        todo.put("doneStatus", true);
        response = Unirest.post("/todos/" + id).body(todo).asJson().getBody().getObject();
    }

    @Then("^the todo with title ([^>]*) will be marked as done on the system$")
    public void theTodoWithTitleSelectedTitleWillBeMarkedAsDoneOnTheSystem(String selectedTitle) {
        assertDoneStatusEquals(findTodoByName(selectedTitle), true);
    }

    @And("^the updated todo will be returned to the user and marked as done$")
    public void theUpdatedTodoWillBeReturnedToTheUserAndMarkedAsDone() {
        assertDoneStatusEquals(response, true);
    }

    @Then("^no todo on the system will be modified$")
    public void noTodoOnTheSystemWillBeModified() {
        assertEquals(originalTodoList, Unirest.get("/todos").asJson().getBody().getObject());
    }

    @And("^the todo will be returned to the user$")
    public void theTodoWillBeReturnedToTheUser() {
        assertEquals(response, originalValue);
    }

    @And("the user will receive an error message that the specified todo does not exist")
    public void theUserWillReceiveAnErrorMessageThatTheSpecifiedTodoDoesNotExist() {
        assertEquals(response.getJSONArray("errorMessages").get(0),
                "No such todo entity instance with GUID or ID -1 found");
    }
}