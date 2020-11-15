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

public class ClassStepDefinition extends BaseStepDefinition {

    @Given("^the following projects exist on the system$")
    public void theFollowingProjectsExistOnTheSystem(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);

        boolean firstLine = true;
        for (List<String> columns : rows) {
            // ignore title row
            if (!firstLine) {
                String title = "\"title\":\"" + columns.get(0) + "\"";
                String completed = "\"completed\":" + columns.get(1);
                String active = "\"active\":" + columns.get(2);
                String description = "\"description\":\"" + columns.get(3) + "\"";
                Unirest.post("/projects")
                        .body("{\n" + title + ",\n" + completed + ",\n" + active + ",\n" + description + "\n}")
                        .asJson();
            }
            firstLine = false;
        }
    }

    @Given("^(.*) is the title of a class on the system$")
    public void projectTitleIsTheTitleOfAClassOnTheSystem(String projectTitle) {
        assertNotNull(findProjectByName(projectTitle));
    }

    @Given("^(.*) is not a title of a class on the system$")
    public void projectTitleIsNotATitleOfAClassOnTheSystem(String projectTitle) {
        assertNull(findProjectByName(projectTitle));
    }
    
    @Given("the course with title {string} is registered in the system:")
    public void the_course_with_title_something_is_registered_in_the_system(String coursetitle) {
        HttpResponse<JsonNode> gResponse = Unirest.post("/projects").body("{\"title\":\"" + coursetitle + "\"}")
                .asJson();
        response = gResponse.getBody().getObject();
        statusCode = gResponse.getStatus();
    }
    
    @Given("the course with {string} is not in the system:")
    public void the_course_with_something_is_not_in_the_system(String coursetitle) {
        assertEquals(null, findProjectByName(coursetitle));
    }

    @Given("the course with title {string}, active status {string} is registered in the system:")
    public void the_course_with_title_something_active_status_something_is_registered_in_the_system(String coursetitle,
            String oldactive) {
        Unirest.post("/projects").body("{\"title\":\"" + coursetitle + "\",\n" + "\"active\":" + oldactive + "\n}")
                .asJson();
    }

    @When("the user requests to add the course with {string} to the system:")
    public void the_user_requests_to_add_the_course_with_something_to_the_system(String coursetitle) {
        the_course_with_title_something_is_registered_in_the_system(coursetitle);
    }
    
    @When("user requests to create a course with title {string} and description {string}")
    public void user_requests_to_create_a_course_with_title_something_and_description_something(String coursetitle,
            String coursedescription) {
        HttpResponse<JsonNode> gResponse = Unirest.post("/projects")
                .body("{\"title\":" + "\"" + coursetitle + "\",\n" + "\"description\":\"" + coursedescription + "\"\n}")
                .asJson();
        response = gResponse.getBody().getObject();
        statusCode = gResponse.getStatus();
    }

    @When("user requests to create a course with title {string} and completed status {string}")
    public void user_requests_to_create_a_course_with_title_something_and_completed_status_something(String coursetitle,
            String completed) {
        HttpResponse<JsonNode> gResponse = Unirest.post("/projects")
                .body("{\"title\":\"" + coursetitle + "\",\n" + "\"completed\":" + completed + "\n}").asJson();
        statusCode = gResponse.getStatus();
    }
    
    @When("user requests to set the the active status of the course with title {string} to {string}")
    public void user_requests_to_set_the_the_active_status_of_the_course_with_title_something_to_something(
            String coursetitle, String newactive) {
        int courseId = findProjectByName(coursetitle).getInt("id");
        Unirest.put("/projects/" + courseId)
                .body("{\"title\":\"" + coursetitle + "\",\n" + "\"active\":" + newactive + "\n}").asJson();
    }
    
    @When("^the user requests the incomplete tasks for the course with title (.*)$")
    public void theUserRequestsTheIncompleteTasksForTheCourseWithTitleProjectTitle(String projectTitle) {
        taskList = new JSONArray();
        JSONArray tasks = getProjectTasks(projectTitle);
        if (tasks == null) {
            // originally called "/projects/-1/tasks" but this returns all tasks for some reason. See bug report
            response = Unirest.get("/projects/-1").asJson().getBody().getObject();
            return;
        }
        for (Object o : tasks) {
            int id = ((JSONObject) o).getInt("id");
            JSONObject todo = (JSONObject) Unirest.get("/todos/" + id).asJson().getBody().getObject()
                    .getJSONArray("todos").get(0);
            if (todo.getString("doneStatus").equalsIgnoreCase("false")) {
                taskList.put(todo);
            }
        }
    }

    @When("user requests to delete the course with title {string}")
    public void user_requests_to_delete_the_course_with_title_something(String coursetitle) {
        JSONObject course = findProjectByName(coursetitle);
        int id = course.getInt("id");
        Unirest.delete("/projects/" + id).header("Content-Type", "application/json").asJson();

    }
    
    @When("user requests to delete a course with title {string}")
    public void user_requests_to_delete_a_course_with_title_something(String invalidtitle) {
        response= Unirest.delete("/projects/-1").asJson().getBody().getObject();
    }

    @Then("the course with title {string} should be removed from the system")
    public void the_course_with_title_something_should_be_removed_from_the_system(String coursetitle) {
        assertEquals(null, findProjectByName(coursetitle));
    }

    @Then("^the system should output an error$")
    public void the_system_should_output_an_errormessage() {
        errorMessage = response.getJSONArray("errorMessages").getString(0);
        assertEquals(errorMessage, "Could not find any instances with projects/-1");
    }
    
    @Then("the course with title {string} and description {string} should be created:")
    public void the_course_with_title_something_and_description_something_should_be_created(String coursetitle,
            String coursedescription) {
        assertEquals(201, statusCode);
    }
    
    @Then("the active status of the course with title {string} should be set to {string}")
    public void the_active_status_of_the_course_with_title_something_should_be_set_to_something(String coursetitle, String newactive){
        JSONObject course = findProjectByName(coursetitle);
        assertEquals(newactive, course.getString("active"));
    }

    @And("^the class with title (.*) has outstanding tasks$")
    public void theClassWithTitleProjectTitleHasOutstandingTasks(String projectTitle) {
        projecteHasOutstandingTasks(projectTitle, true);
    }

    @And("^the class with the title (.*) has outstanding tasks$")
    public void theClassWithTheTitleProjectTitleHasOutstandingTasks(String projectTitle) {
        projecteHasOutstandingTasks(projectTitle, false);
    }

    @And("^the class with title (.*) has no outstanding tasks$")
    public void theClassWithTitleProjectTitleHasNoOutstandingTasks(String projectTitle) {
        JSONArray tasks = getProjectTasks(projectTitle);
        for (Object o : tasks) {
            int id = ((JSONObject)o).getInt("id");
            JSONObject todo = (JSONObject) Unirest.get("/todos/" + id)
                    .asJson().getBody().getObject()
                    .getJSONArray("todos").get(0);
            if (todo.getString("doneStatus").equalsIgnoreCase("false")) {
                fail();
            }
        }
    }

    @And("^the class with title (.*) has no tasks$")
    public void theClassWithTitleProjectTitleHasNoTasks(String projectTitle) {
        JSONArray tasks = getProjectTasks(projectTitle);
        assertEquals(tasks.length(), 0);
    }

    @And("the user will receive an error telling them that the course doesn't exist on the system")
    public void theUserWillReceiveAnErrorTellingThemThatTheTaskDoesntExistOnTheSystem() {
        String err = response.getJSONArray("errorMessages").getString(0);
        assertEquals("Could not find an instance with projects/-1", err);
    }

    @And("that the todos with title {string} being a task of {string}")
    public void that_the_todos_with_title_something_being_a_task_of_something(String todotitle, String coursetitle) {

        Unirest.post("/projects").body("{\"title\":\"" + coursetitle + "\"}").asJson();
        Unirest.post("/projects" + findProjectByName(coursetitle).getInt("id") + "/tasks")
                .body("{\"title\":\"" + todotitle + "\"}").asJson();

    }

    @And("the user requests to add the todo with name {string} and description {string} to the course {string}")
    public void the_user_requests_to_add_the_todo_with_name_something_and_description_something_to_the_course_something(String todotitle, String tododescription, String coursetitle){
        Unirest.post("/projects/"+findProjectByName(coursetitle).getInt("id")+"/tasks").body("{\"title\":\"" + todotitle+"\"}").asJson();
    }

    @And("the only course in the database is the course with title {string}")
    public void the_only_course_in_the_database_is_the_course_with_title_something(String coursetitle){
        the_course_with_title_something_is_registered_in_the_system(coursetitle);
        int length = Unirest.get("/projects").asJson().getBody().getObject().getJSONArray("projects").length();
        assertEquals(2, length);
    }
}