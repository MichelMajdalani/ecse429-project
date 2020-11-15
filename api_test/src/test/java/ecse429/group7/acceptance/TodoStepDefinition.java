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

public class TodoStepDefinition extends BaseStepDefinition {

    @Given("the todo with name {string}, done status {string} and description {string} is registered in the system:")
    public void the_todo_with_name_done_status_and_description_is_registered_in_the_system(String todotitle,
            String tododonestatus, String tododescription) {
        Unirest.post("/todos").body("{\"title\":\"" + todotitle.replace("\"", "") + "\",\"doneStatus\":"
                + tododonestatus.replace("\"", "") + ",\"description\":\"" + tododescription.replace("\"", "") + "\"}")
                .asJson();
    }

    @Given("^the following todo is registered in the system:$")
    @And("^the following todos registered in the system$")
    public void theFollowingTodosRegisteredInTheSystem(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);

        boolean firstLine = true;
        for (List<String> columns : rows) {
            // ignore title row
            if(!firstLine) {
                addTodoByRow(columns);
            }
            firstLine = false;
        }
    }

    @Given("^(.*) is the title of a todo registered on the system$")
    public void selectedTitleIsTheTitleOfATodoRegisteredOnTheSystem(String selectedTitle) {
        assertNotNull(findTodoByName(selectedTitle));
    }

    @Given("^(.*) is not a title of a todo registered on the system$")
    public void selectedTitleIsNotTheTitleOfATodoRegisteredOnTheSystem(String selectedTitle) {
        assertNull(findTodoByName(selectedTitle));
    }

    @Given("the todo with name {string} and description {string} is registered in the system:")
    public void the_todo_with_name_something_and_description_something_is_registered_in_the_system(String todotitle, String tododescription){
        Unirest.post("/todos")
        .body("{\"title\":\"" + todotitle + "\",\n\"description\":\"" + tododescription+ "\"\n}")
        .asJson();
    }

    @Given("the todo with name {string} is registered in the system:")
    public void the_todo_with_name_something_is_registered_in_the_system(String todotitle) {
        Unirest.post("/todos")
        .body("{\"title\":\"" + todotitle + "\"}")
        .asJson();
    }

    @Given("^the todo with name (.+), status (.+), description (.+) is registered in the system$")
    public void the_todo_with_name_status_description_is_registered_in_the_system(String todotitle,
            String tododonestatus, String tododescription) {
        the_todo_with_name_done_status_and_description_is_registered_in_the_system(todotitle, tododonestatus,
                tododescription);
    }
    
    @When("the user requests to add the todo with name {string} and description {string} to the course with title {string}")
    public void the_user_requests_to_add_the_todo_with_name_something_and_description_something_to_the_course_with_title_something(String todotitle, String tododescription, String coursetitle){
        int todoId= findIdFromTodoName(todotitle);
        Unirest.post("/todos/"+todoId+"/tasksof").body("{\"title\":\"" + coursetitle + "\"}").asJson();
    }

    @When("user requests to add the todo with name {string} to the project title {string}")
    public void user_requests_to_add_the_todo_with_name_something_to_the_project_title_something(String todotitle,
            String inavlidcoursettitle) {
        JSONObject course = findProjectByName(inavlidcoursettitle);
        if (course == null) {
            statusCode = Unirest.get("/projects/-1").asJson().getStatus();
        }
    }
    
    @When("^the user chooses to mark the task named (.*) as done$")
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

    @When("^the user requests to set the description of the todo with title \"([^\"]*)\" to \"([^\"]*)\"$")
    public void theUserRequestsToSetTheDescriptionOfTheTodoWithTitleSelectedTitleToNewDescription(
            String selectedTitle, String newDescription) {
        originalTodoList = Unirest.get("/todos").asJson().getBody().getObject();
        JSONObject todo = findTodoByName(selectedTitle);
        if (todo == null) {
            response = Unirest.post("/todos/-1").asJson().getBody().getObject();
            return;
        }
        originalValue = new JSONObject(todo.toString());
        int id = todo.getInt("id");
        todo.remove("id");
        todo.put("description", newDescription);
        todo.put("doneStatus", todo.getString("doneStatus").equalsIgnoreCase("true"));
        response = Unirest.put("/todos/" + id).body(todo).asJson().getBody().getObject();
    }

    @When("^the user requests to remove the description of the todo with title (.*)$")
    public void theUserRequestsToRemoveTheDescriptionOfTheTodoWithTitleSelectedTitle(String selectedTitle) {
        theUserRequestsToSetTheDescriptionOfTheTodoWithTitleSelectedTitleToNewDescription(selectedTitle, null);
    }

    @When("^the user requests to change the description of the todo with title (.*)$")
    public void theUserRequestsToChangeTheDescriptionOfTheTodoWithTitleSelectedTitle(String selectedTitle) {
        theUserRequestsToSetTheDescriptionOfTheTodoWithTitleSelectedTitleToNewDescription(selectedTitle,
                "Default new value (doesn't matter)");
    }
    
    @When("^the student requests to delete an existing task with title (.+)$")
    public void the_students_requests_to_delete_an_existing_task_with_title_something(String todotitle) {
        int id = findIdFromTodoName(todotitle);
        HttpResponse<JsonNode> gResponse = Unirest.delete("/todos/" + id).asJson();
        statusCode = gResponse.getStatus();
        response = gResponse.getBody().getObject();
    }

    @When("^the student requests to delete all tasks from (.+)$")
    public void the_students_requests_to_delete_all_tasks_from(String projecttitle) {
        taskList = new JSONArray();
        JSONArray tasks = getProjectTasks(projecttitle);
        for (Object o : tasks) {
            int id = ((JSONObject) o).getInt("id");
            HttpResponse<JsonNode> gResponse = Unirest.delete("/todos/" + id).asJson();
            statusCode = gResponse.getStatus();
            response = gResponse.getBody().getObject();
            counter++;
        }
    }

    @When("user requests to delete todos task of {string}")
    public void user_requests_to_delete_todos_task_of_something(String coursettitle) {
        the_students_requests_to_delete_all_tasks_from(coursettitle);
    }

    @Then("the todos task of {string} should be removed")
    public void the_todos_task_of_something_should_be_removed(String coursettitle) {
        assertEquals(0, getProjectTasks(coursettitle).length());
    }
    
    @Then("^the (.+) todos from (.+) are removed$")
    public void the_todos_from_are_removed(String n, String projecttitle) {
        assertEquals(Integer.parseInt(n), counter);
        // Could check if todo empty
    }

    @Then("^the description of the todo with title (.*) will be removed$")
    public void theDescriptionOfTheTodoWillBeRemoved(String selectedTitle) {
        assertEquals(findTodoByName(selectedTitle).getString("description"), "");
    }

    @Then("^the description of the todo with title \"([^\"]*)\" will be changed to \"([^\"]*)\"$")
    public void theDescriptionOfTheTodoWillBeChangedToNewDescription(String selectedTitle, String newDescription) {
        assertEquals(findTodoByName(selectedTitle).getString("description"), newDescription);
    }

    @Then("^(.*) todos will be returned$")
    public void nTodosWillBeReturned(int n) {
        assertEquals(n, taskList.length());
    }

    @Then("^the todo with title (.*) will be marked as done on the system$")
    public void theTodoWithTitleSelectedTitleWillBeMarkedAsDoneOnTheSystem(String selectedTitle) {
        assertDoneStatusEquals(findTodoByName(selectedTitle), true);
    }

    @Then("the todo with name {string} should be added to the todo list of the course with title {string}")
    public void the_todo_with_name_something_should_be_added_to_the_todo_list_of_the_course_with_title_something(
            String todotitle, String coursetitle) {
        assertEquals(1, getProjectTasks(coursetitle).length());
    }

    @Then("^no todo on the system will be modified$")
    public void noTodoOnTheSystemWillBeModified() {
        assertEquals(originalTodoList, Unirest.get("/todos").asJson().getBody().getObject());
    }

    @And("^the todo with title (.*) is not marked as done$")
    public void theTodoWithTitleSelectedTitleIsNotMarkedAsDone(String selectedTitle) {
        assertDoneStatusEquals(findTodoByName(selectedTitle), false);
    }

    @And("^the todo with title (.*) is marked as done$")
    public void theTodoWithTitleSelectedTitleIsMarkedAsDone(String selectedTitle) {
        assertDoneStatusEquals(findTodoByName(selectedTitle), true);
    }

    @And("^the updated todo will be returned to the user and marked as done$")
    public void theUpdatedTodoWillBeReturnedToTheUserAndMarkedAsDone() {
        assertDoneStatusEquals(response, true);
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

    @And("the following todos are associated with {string}")
    public void theFollowingTodosAreAssociatedWithClass(String className, DataTable table) {
        List<List<String>> rows = table.asLists(String.class);
        int projId = findProjectByName(className).getInt("id");
        boolean firstLine = true;
        for (List<String> columns : rows) {
            // ignore title row
            if(!firstLine) {
                int id = addTodoByRow(columns).getInt("id");
                Unirest.post("/todos/" + id + "/tasksof")
                        .body("{\"id\":\"" + projId + "\"}")
                        .asJson();
            }
            firstLine = false;
        }
    }

    @And("no todos are associated with {string}")
    public void noTodosAreAssociatedWithFACC(String className) {
        assertEquals(getProjectTasks(className).length(), 0);
    }

    @And("^each todo returned will be marked as done$")
    public void eachTodoReturnedWillBeMarkedAsDone() {
        for (Object o : taskList) {
            JSONObject todo = (JSONObject) o;
            assertDoneStatusEquals(todo, false);
        }
    }

    @And("^each todo returned will be a task of the class with title (.*)$")
    public void eachTodoReturnedWillBeATaskOfTheClassWithTitleProjectTitle(String projectTitle) {
        JSONArray tasks = getProjectTasks(projectTitle);
        Set<Integer> taskIDs = new HashSet<>();
        for (Object o : tasks) {
            JSONObject task = (JSONObject) o;
            taskIDs.add(task.getInt("id"));
        }

        for (Object o : taskList) {
            JSONObject todo = (JSONObject) o;
            assertTrue(taskIDs.contains(todo.getInt("id")));
        }
    }

    @And("^the user will be given the updated version of the todo where the description is (.*)$")
    public void theUserWillBeGivenTheUpdatedVersionOfTheTodoWhereTheDescriptionIsNewDescription(String newDescription) {
        assertEquals(response.getString("description"), newDescription);
    }

    @And("the user will be given the update version of the todo with an empty description")
    public void theUserWillBeGivenTheUpdateVersionOfTheTodoWithAnEmptyDescription() {
        assertEquals(response.getString("description"), "");
    }
}