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
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PriorityStepDefinition extends BaseTest {

    String errorMessage;
    JSONObject originalValue;
    JSONObject response;
    JSONObject originalTodoList;
    JSONArray incompleteTasks;

    @Before
    public void initVars() {
        Unirest.config().defaultBaseUrl(BASE_URL);
        startServer();
        errorMessage = "";
        response = null;
        originalValue = null;
        originalTodoList = null;
        incompleteTasks = null;
    }

    @After
    public void after() {
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

    @Given("the todo with name {string}, done status {string} and description {string} is registered in the system:")
    public void the_todo_with_name_done_status_and_description_is_registered_in_the_system(String todotitle, String tododonestatus, String tododescription) {
        Unirest.post("/todos")
                        .body("{\"title\":\"" + todotitle.replace("\"", "") + "\",\"doneStatus\":"
                                + tododonestatus.replace("\"", "") + ",\"description\":\"" + tododescription.replace("\"", "") + "\"}")
                        .asJson();
    }

    @When("user requests to categorize todo with title {string} as {string} priority")
    public void when_user_requests_to_categorize_todo_with_title_as_priority(String todotitle, String prioritytoassign) {
        // Find ID of Task todo_title
        int id = findIdFromTodoName(todotitle.replace("\"", ""));

        HttpResponse<JsonNode> response = Unirest.post("/todos/" + id +"/categories")
                .body("{\n\"title\":\"" + prioritytoassign.replace("\"", "") + "\"\n}\n").asJson();

        if(response.getStatus() != 200 && response.getStatus() != 201) {
            errorMessage = response.getBody().getObject().getJSONArray("errorMessages").getString(0);
        }
    }

    @When("^user requests to remove (.+) priority categorization from (.+)$")
    public void user_requests_to_remove_priority_categorization_from(String oldpriority, String todotitle) {
        int category_id = findIdFromTodoCategoryName(oldpriority.replace("\"", ""), todotitle.replace("\"", ""));
        int todo_id = findIdFromTodoName(todotitle.replace("\"", ""));
        
        Unirest.delete("/todos/" + todo_id + "/categories/" + category_id)
                .header("Content-Type", "application/json")
        .asJson();
    }

    @Then("^the (.+) should be classified as a (.+) priority task$")
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
    public void the_todo_is_assigned_as_a_priority_task(String todotitle, String originalpriority) throws Throwable {
        when_user_requests_to_categorize_todo_with_title_as_priority(todotitle, originalpriority);
    }

    @Given("^the API server is running$")
    public void theAPIServerIsRunning() {
        waitUntilOnline();
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

    public static JSONObject addTodoByRow(List<String> columns) {
        String title = "\"title\":\"" + columns.get(0) + "\"";
        String doneStatus = "\"doneStatus\":" + columns.get(1);
        String description = "\"description\":\"" + columns.get(2) + "\"";
        return Unirest.post("/todos")
                .body("{\n" + title + ",\n" + doneStatus + ",\n" + description + "\n}")
                .asJson().getBody().getObject();
    }

    public static void assertDoneStatusEquals(JSONObject todo, boolean val) {
        assertNotNull(todo);
        assertTrue(todo.getString("doneStatus").equalsIgnoreCase(val + ""));
    }

    @Given("^(.*) is the title of a todo registered on the system$")
    public void selectedTitleIsTheTitleOfATodoRegisteredOnTheSystem(String selectedTitle) {
        assertNotNull(findTodoByName(selectedTitle));
    }

    @Given("^(.*) is not a title of a todo registered on the system$")
    public void selectedTitleIsNotTheTitleOfATodoRegisteredOnTheSystem(String selectedTitle) {
        assertNull(findTodoByName(selectedTitle));
    }

    @And("^the todo with title (.*) is not marked as done$")
    public void theTodoWithTitleSelectedTitleIsNotMarkedAsDone(String selectedTitle) {
        assertDoneStatusEquals(findTodoByName(selectedTitle), false);
    }

    @And("^the todo with title (.*) is marked as done$")
    public void theTodoWithTitleSelectedTitleIsMarkedAsDone(String selectedTitle) {
        assertDoneStatusEquals(findTodoByName(selectedTitle), true);
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

    @Then("^the todo with title (.*) will be marked as done on the system$")
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

    @Given("^the following projects exist on the system$")
    public void theFollowingProjectsExistOnTheSystem(DataTable table) {
        List<List<String>> rows = table.asLists(String.class);

        boolean firstLine = true;
        for (List<String> columns : rows) {
            // ignore title row
            if(!firstLine) {
                String title = "\"title\":\"" + columns.get(0) + "\"";
                String completed = "\"completed\":" + columns.get(1);
                String active = "\"active\":" + columns.get(2);
                String description = "\"description\":\"" + columns.get(3) + "\"";
                Unirest.post("/projects")
                        .body("{\n" + title + ",\n" + completed + ",\n"
                                + active + ",\n"
                                + description + "\n}")
                        .asJson();
            }
            firstLine = false;
        }
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

    @Given("^(.*) is the title of a class on the system$")
    public void projectTitleIsTheTitleOfAClassOnTheSystem(String projectTitle) {
        assertNotNull(findProjectByName(projectTitle));
    }

    @Given("^(.*) is not a title of a class on the system$")
    public void projectTitleIsNotATitleOfAClassOnTheSystem(String projectTitle) {
        assertNull(findProjectByName(projectTitle));
    }

    @And("^the class with title (.*) has outstanding tasks$")
    public void theClassWithTitleProjectTitleHasOutstandingTasks(String projectTitle) {
        JSONArray projects = getProjectTasks(projectTitle);
        for (Object o : projects) {
            int id = ((JSONObject)o).getInt("id");
            JSONObject todo = (JSONObject) Unirest.get("/todos/" + id)
                    .asJson().getBody().getObject()
                    .getJSONArray("todos").get(0);
            if (todo.getString("doneStatus").equalsIgnoreCase("false")) {
                return;
            }
        }
        fail();
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

    @When("^the user requests the incomplete tasks for the course with title (.*)$")
    public void theUserRequestsTheIncompleteTasksForTheCourseWithTitleProjectTitle(String projectTitle) {
        incompleteTasks = new JSONArray();
        JSONArray tasks = getProjectTasks(projectTitle);
        if (tasks == null) {
            response = Unirest.get("/projects/-1/tasks")
                    .asJson().getBody().getObject();
            return;
        }
        for (Object o : tasks) {
            int id = ((JSONObject)o).getInt("id");
            JSONObject todo = (JSONObject) Unirest.get("/todos/" + id)
                    .asJson().getBody().getObject()
                    .getJSONArray("todos").get(0);
            if (todo.getString("doneStatus").equalsIgnoreCase("false")) {
                incompleteTasks.put(todo);
            }
        }
    }

    @Then("^(.*) todos will be returned$")
    public void nTodosWillBeReturned(int n) {
        assertEquals(n, incompleteTasks.length());
    }

    @And("^each todo returned will be marked as done$")
    public void eachTodoReturnedWillBeMarkedAsDone() {
        for (Object o : incompleteTasks) {
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

        for (Object o : incompleteTasks) {
            JSONObject todo = (JSONObject) o;
            assertTrue(taskIDs.contains(todo.getInt("id")));
        }
    }

    @And("the user will receive an error telling them that the course doesn't exist on the system")
    public void theUserWillReceiveAnErrorTellingThemThatTheTaskDoesntExistOnTheSystem() {
        System.out.println(response.toString());
        //TODO: Handle the fact that this behavior has bugs
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

    @Then("^the description of the todo with title \"([^\"]*)\" will be changed to \"([^\"]*)\"$")
    public void theDescriptionOfTheTodoWillBeChangedToNewDescription(String selectedTitle, String newDescription) {
        assertEquals(findTodoByName(selectedTitle).getString("description"), newDescription);
    }

    @And("^the user will be given the updated version of the todo where the description is (.*)$")
    public void theUserWillBeGivenTheUpdatedVersionOfTheTodoWhereTheDescriptionIsNewDescription(String newDescription) {
        assertEquals(response.getString("description"), newDescription);
    }

    @When("^the user requests to remove the description of the todo with title (.*)$")
    public void theUserRequestsToRemoveTheDescriptionOfTheTodoWithTitleSelectedTitle(String selectedTitle) {
        theUserRequestsToSetTheDescriptionOfTheTodoWithTitleSelectedTitleToNewDescription(selectedTitle, null);
    }

    @Then("^the description of the todo with title (.*) will be removed$")
    public void theDescriptionOfTheTodoWillBeRemoved(String selectedTitle) {
        assertEquals(findTodoByName(selectedTitle).getString("description"), "");
    }

    @And("the user will be given the update version of the todo with an empty description")
    public void theUserWillBeGivenTheUpdateVersionOfTheTodoWithAnEmptyDescription() {
        assertEquals(response.getString("description"), "");
    }

    @When("^the user requests to change the description of the todo with title (.*)$")
    public void theUserRequestsToChangeTheDescriptionOfTheTodoWithTitleSelectedTitle(String selectedTitle) {
        theUserRequestsToSetTheDescriptionOfTheTodoWithTitleSelectedTitleToNewDescription(
                selectedTitle,
                "Default new value (doesn't matter)"
        );
    }
}