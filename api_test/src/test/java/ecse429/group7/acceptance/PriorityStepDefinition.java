package ecse429.group7.acceptance;


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
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PriorityStepDefinition extends BaseStepDefinition {

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

    @When("user requests to categorize todo with title {string} as {string} priority")
    public void when_user_requests_to_categorize_todo_with_title_as_priority(String todotitle, String prioritytoassign) {
        request_priority_for_todo(todotitle, prioritytoassign);
    }

    @When("^user requests to remove (.+) priority categorization from (.+)$")
    public void user_requests_to_remove_priority_categorization_from(String oldpriority, String todotitle) {
        int category_id = findIdFromTodoCategoryName(oldpriority.replace("\"", ""), todotitle.replace("\"", ""));
        int todo_id = findIdFromTodoName(todotitle.replace("\"", ""));

        Unirest.delete("/todos/" + todo_id + "/categories/" + category_id).header("Content-Type", "application/json")
                .asJson();
    }

    @When("^the user requests the incomplete HIGH priority tasks for the course with title (.+)$")
    public void the_students_queries_all_incomplete_tasks_with_high_priority_from_a_course_with_title(String projecttitle) {
        taskList = new JSONArray();
        JSONArray tasks = getProjectTasks(projecttitle);
        if (tasks == null) {
            //Problem: /projects/-1/tasks is a known bug, using /projects/-1 instead to show error
            response = Unirest.get("/projects/-1")
                    .asJson().getBody().getObject();
            return;
        }
        for (Object o : tasks) {
            int id = ((JSONObject)o).getInt("id");
            JSONObject todo = (JSONObject) Unirest.get("/todos/" + id)
                    .asJson().getBody().getObject()
                    .getJSONArray("todos").get(0);
            int priorityID = ((JSONObject) ((JSONArray) todo.get("categories")).get(0)).getInt("id");
            String category = (String) ((JSONObject) ((JSONArray) ( Unirest.get("/categories/" + priorityID).asJson().getBody().getObject()).get("categories")).get(0)).get("title");
            if (todo.getString("doneStatus").equalsIgnoreCase("false") && category.equalsIgnoreCase("HIGH")) {
                taskList.put(todo);
            }
        }
    }

    @When("^user requests to update the priority categorization of the todo with title (.+) from (.+) to (.+)$")
    public void user_requests_to_update_the_priority_categorization_of_the_todo_with_title_from_to(String todotitle, String todoprioritytask, String todoupdatedprioritytask) {
        user_requests_to_remove_priority_categorization_from(todoprioritytask, todotitle);
        request_priority_for_todo(todotitle, todoupdatedprioritytask);
    }

    @When("^user requests to add a priority categorization of (.+) to the todo with title (.+) with (.+)$")
    public void user_requests_to_add_a_priority_categorization_of_to_the_todo_with_title_with(
            String todonewprioritytask, String todotitle, String todoprioritytask) {
        request_priority_for_todo(todotitle, todonewprioritytask);
    }

    @Then("^the todo with title \"([^\"]*)\" should be classified as a \"([^\"]*)\" priority task$")
    public void the_todo_with_title_should_be_classified_as_a_priority_task(String todotitle, String todoupdatedprioritytask) {
        the_should_be_classified_as_a_priority_task(todotitle, todoupdatedprioritytask);
    }

    @Then("^the \"([^\"]*)\" should be classified as a \"([^\"]*)\" priority task$")
    public void the_should_be_classified_as_a_priority_task(String todotitle, String prioritytoassign) {
        int category_id = findIdFromTodoCategoryName(prioritytoassign.replace("\"", ""), todotitle.replace("\"", ""));

        // if != -1, then found correct category (priority) with correct todo name 
        assertTrue(category_id != -1);
    }

    @Then("^the system should output an error message$")
    public void the_system_should_output_an_error_message() {
        assertEquals(errorMessage, "Could not find parent thing for relationship todos/-1/categories");
    }

    @And("^the todo (.+) is assigned as a (.+) priority task$")
    public void the_todo_is_assigned_as_a_priority_task(String todotitle, String originalpriority) {
        request_priority_for_todo(todotitle, originalpriority);
    }

    @And("^the following priorities are registered in the system:$")
    public void the_following_priorities_are_registered_in_the_system(DataTable table) {
        the_following_categories_are_registered_in_the_todo_manager_restapi_system(table);
    }

    @And("^the todo with title (.+) is assigned as a (.+)$")
    public void the_todo_with_title_is_assigned_as_a(String todotitle, String todoprioritytask) {
        request_priority_for_todo(todotitle, todoprioritytask);
    }

    @And("^each todo returned will have a HIGH priority$")
    public void each_todo_returned_will_have_a_high_priority() {
        // Incomplete High priority is a subset of incoomplete
        // Incomplete is reset to the incomplete high priority states in the when
        for (Object o : taskList) {
            JSONObject todo = (JSONObject) o;
            int priorityID = ((JSONObject) ((JSONArray) todo.get("categories")).get(0)).getInt("id");
            String category = (String) ((JSONObject) ((JSONArray) (Unirest.get("/categories/" + priorityID).asJson().getBody().getObject()).get("categories")).get(0)).get("title");
            assertEquals(category, "HIGH");
        }
    }
}