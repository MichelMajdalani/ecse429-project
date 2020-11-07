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

import static org.junit.Assert.*;

import java.util.List;
import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;


public class PriorityStepDefinition extends BaseTest {

    String errorMessage;

    @Given("^the following categories are registered in the todoManagerRestAPI system:$")
    public void the_following_categories_are_registered_in_the_todomanagerrestapi_system(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
    
        int idx = 0;
        for (List<String> columns : rows) {
            // ignore title row
            if(idx != 0)
            {
                HttpResponse<JsonNode> response = Unirest.post("/categories")
                    .body("{\n\"description\":\"" + columns.get(1) + "\",\n  \"title\":\"" + columns.get(0) + "\"\n}").asJson();
        
            }
            idx++;
        }
    }

    @Given("^the following todo is registered in the system:$")
    public void the_following_todo_is_registered_in_the_system(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
    
        int idx = 0;
        for (List<String> columns : rows) {
            // ignore title row
            if(idx != 0)
            {
                HttpResponse<JsonNode> response = Unirest.post("/todos")
                .body("{\"title\":\"" + columns.get(0) + "\",\"doneStatus\":" + columns.get(1) + ",\"description\":\"" + columns.get(2) + "\"}").asJson();
            }
            idx++;
        }
    }

    @When("^user requests to categorize todo with title \"([^\"]*)\" as \"([^\"]*)\" priority$")
    public void user_requests_to_categorize_todo_with_title_something_as_something_priority(String todo_title, String priority) {
        // Find ID of Task todo_title
        int id = findIdFromTodoName(todo_title);
 
        HttpResponse<JsonNode> response = 
        Unirest.post("/todos/" + String.valueOf(id) +"/categories").body("{\n\"title\":\"" + priority + "\"\n}\n").asJson();

        if(response.getStatus() != 200 && response.getStatus() != 201)
        {
            errorMessage = response.getBody().getObject().getJSONArray("errorMessages").getString(0);
         }     

    }

    @When("^user requests to remove \"([^\"]*)\" priority categorization from \"([^\"]*)\"$")
    public void user_requests_to_remove_something_priority_categorization_from_something(String priority, String task) {
        int category_id = findIdFromTodoCategoryName(priority, task);
        int todo_id = findIdFromTodoName(task);
        
        HttpResponse<JsonNode> response = Unirest.delete("/todos/" + String.valueOf(todo_id) + "/categories/" + String.valueOf(category_id)).header("Content-Type", "application/json")
        .asJson();
    }

    @When("^user requests to add \"([^\"]*)\" priority categorization to \"([^\"]*)\"$")
    public void user_requests_to_add_something_priority_categorization_to_something(String priority, String todo_title) {
        user_requests_to_categorize_todo_with_title_something_as_something_priority(todo_title, priority);
    }

    @Then("^the \"([^\"]*)\" should be classified as a \"([^\"]*)\" priority task$")
    public void the_something_should_be_classified_as_a_something_priority_task(String todo, String priority) throws Throwable {
        int category_id = findIdFromTodoCategoryName(priority, todo);

        // if != -1, then found category with name 
        assertTrue(category_id != -1);
    }

    @Then("^the system should output an error message$")
    public void the_system_should_output_an_error_message() {
        assertEquals(errorMessage, "Could not find parent thing for relationship todos/-1/categories");
    }

    @And("^the todo \"([^\"]*)\" is assigned as a \"([^\"]*)\" priority task$")
    public void the_todo_something_is_assigned_as_a_something_priority_task(String todo_title, String priority) throws Throwable {
        user_requests_to_categorize_todo_with_title_something_as_something_priority(todo_title, priority);
    }

    @Before
    public static void before()
    {
        while(!startServer());
        try{

            Thread.sleep(500);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @After
    public static void after()
    {
        try {
            Unirest.get("/shutdown").asJson();
        } catch (Exception ignored) {}
    }

}