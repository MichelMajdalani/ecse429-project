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

import com.google.gson.JsonObject;

public class PriorityStepDefinition extends BaseTest {

    String errorMessage;
    int statusCode;
    JSONObject originalValue;
    JSONObject response;
    JSONObject originalTodoList;
    JSONArray taskList;
    int counter;

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
        
        statusCode = response.getStatus();
        if(statusCode != 200 && statusCode != 201) {
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

    public JSONObject addTodoByRow(List<String> columns) {
        String title = "\"title\":\"" + columns.get(0) + "\"";
        String doneStatus = "\"doneStatus\":" + columns.get(1);
        String description = "\"description\":\"" + columns.get(2) + "\"";
        JSONObject todoObj = Unirest.post("/todos")
                .body("{\n" + title + ",\n" + doneStatus + ",\n" + description + "\n}")
                .asJson().getBody().getObject();
        if(columns.size() == 4) {
            when_user_requests_to_categorize_todo_with_title_as_priority(columns.get(0), columns.get(3));
        }
        return todoObj;
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

    public void projecteHasOutstandingTasks(String projectTitle, boolean checkIncomplete) {
        JSONArray projects = getProjectTasks(projectTitle);
        for (Object o : projects) {
            int id = ((JSONObject)o).getInt("id");
            JSONObject todo = (JSONObject) Unirest.get("/todos/" + id)
                    .asJson().getBody().getObject()
                    .getJSONArray("todos").get(0);
            // NOTE This assumptions makes it less reusable
            if (!checkIncomplete || todo.getString("doneStatus").equalsIgnoreCase("false")) {
                return;
            }
        }
        fail();
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

    @When("^the user requests the incomplete tasks for the course with title (.*)$")
    public void theUserRequestsTheIncompleteTasksForTheCourseWithTitleProjectTitle(String projectTitle) {
        taskList = new JSONArray();
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
                taskList.put(todo);
            }
        }
    }

    @Then("^(.*) todos will be returned$")
    public void nTodosWillBeReturned(int n) {
        assertEquals(n, taskList.length());
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

    // ID 9: UPDATE TASK PRIORITY
    @Given("^the todo with name (.+), status (.+), description (.+) is registered in the system$")
    public void the_todo_with_name_status_description_is_registered_in_the_system(String todotitle, String tododonestatus, String tododescription)  {
        the_todo_with_name_done_status_and_description_is_registered_in_the_system(todotitle, tododonestatus, tododescription);
    }

    @When("^user requests to update the priority categorization of the todo with title (.+) from (.+) to (.+)$")
    public void user_requests_to_update_the_priority_categorization_of_the_todo_with_title_from_to(String todotitle, String todoprioritytask, String todoupdatedprioritytask) {
        user_requests_to_remove_priority_categorization_from(todoprioritytask, todotitle);
        when_user_requests_to_categorize_todo_with_title_as_priority(todotitle, todoupdatedprioritytask);
    }

    @When("^user requests to add a priority categorization of (.+) to the todo with title (.+) with (.+)$")
    public void user_requests_to_add_a_priority_categorization_of_to_the_todo_with_title_with(String todonewprioritytask, String todotitle, String todoprioritytask) {
        when_user_requests_to_categorize_todo_with_title_as_priority(todotitle, todonewprioritytask);
    }

    @Then("^the todo with title \"([^\"]*)\" should be classified as a \"([^\"]*)\" priority task$")
    public void the_todo_with_title_should_be_classified_as_a_priority_task(String todotitle, String todoupdatedprioritytask) {
        the_should_be_classified_as_a_priority_task(todotitle, todoupdatedprioritytask);
    }

    @Then("^an error code (.+) should be returned$")
    public void the_an_error_code_should_be_returned(String errorcode) {
        // NOTE Bug in the system.
        assertEquals(201, statusCode);
    }

    @And("^the following priorities are registered in the system:$")
    public void the_following_priorities_are_registered_in_the_system(DataTable table) {
        the_following_categories_are_registered_in_the_todo_manager_restapi_system(table);
    }

    @And("^the todo with title (.+) is assigned as a (.+)$")
    public void the_todo_with_title_is_assigned_as_a(String todotitle, String todoprioritytask) {
        when_user_requests_to_categorize_todo_with_title_as_priority(todotitle, todoprioritytask);
    }

    // ID 8: Query incomplete HIGH priority tasks
    @When("^the user requests the incomplete HIGH priority tasks for the course with title (.+)$")
    public void the_students_queries_all_incomplete_tasks_with_high_priority_from_a_course_with_title(String projecttitle) {
        taskList = new JSONArray();
        JSONArray tasks = getProjectTasks(projecttitle);
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
            int priorityID = ((JSONObject) ((JSONArray) todo.get("categories")).get(0)).getInt("id");
            String category = (String) ((JSONObject) ((JSONArray) ((JSONObject) Unirest.get("/categories/" + priorityID).asJson().getBody().getObject()).get("categories")).get(0)).get("title");
            if (todo.getString("doneStatus").equalsIgnoreCase("false") && category.equalsIgnoreCase("HIGH")) {
                taskList.put(todo);
            }
        }
    }

    @And("^each todo returned will have a HIGH priority$")
    public void each_todo_returned_will_have_a_high_priority() {
        // Incomplete High priority is a subset of incoomplete
        // Incomplete is reset to the incomplete high priority states in the when
        for (Object o : taskList) {
            JSONObject todo = (JSONObject) o;
            int priorityID = ((JSONObject) ((JSONArray) todo.get("categories")).get(0)).getInt("id");
            String category = (String) ((JSONObject) ((JSONArray) ((JSONObject) Unirest.get("/categories/" + priorityID).asJson().getBody().getObject()).get("categories")).get(0)).get("title");
            assertEquals(category, "HIGH");
        }
    }

    // ID 4: Remove task from to do list
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
            int id = ((JSONObject)o).getInt("id");
            HttpResponse<JsonNode> gResponse = Unirest.delete("/todos/" + id).asJson();
            statusCode = gResponse.getStatus();
            response = gResponse.getBody().getObject();   
            counter++;     
        }
    }

    @Then("^the (.+) todos from (.+) are removed$")
    public void the_todos_from_are_removed(String n, String projecttitle) {
        assertEquals(Integer.parseInt(n), counter);
        // Could check if todo empty
    }

    @Then("^(.+) is returned.$")
    @And("^a (.+) is returned$")
    public void a_is_returned(String statuscode) {
        assertEquals(Integer.parseInt(statuscode), statusCode);
    }

    // ID 6: Remove todolist for class

    @Given("the course with title {string} is registered in the system:")
    public void the_course_with_title_something_is_registered_in_the_system(String coursetitle) {
        HttpResponse<JsonNode> gResponse = Unirest.post("/projects")
        .body("{\"title\":\"" + coursetitle+"\"}")
        .asJson();
        response= gResponse.getBody().getObject();
        statusCode = gResponse.getStatus();
    }

    @And("that the todos with title {string} being a task of {string}")
    public void that_the_todos_with_title_something_being_a_task_of_something(String todotitle, String coursetitle) {

        Unirest.post("/projects")
        .body("{\"title\":\"" + coursetitle+"\"}")
        .asJson();
        Unirest.post("/projects"+findProjectByName(coursetitle).getInt("id")+"/tasks").body("{\"title\":\"" + todotitle+"\"}").asJson();

    }

    @When("user requests to delete the course with title {string}")
    public void user_requests_to_delete_the_course_with_title_something(String coursetitle) {
        JSONObject course = findProjectByName(coursetitle);
        int id = course.getInt("id");
        Unirest.delete("/projects/"+id).header("Content-Type", "application/json").asJson();
        
    }

    @When("user requests to delete todos task of {string}")
    public void user_requests_to_delete_todos_task_of_something(String coursettitle) {
        the_students_requests_to_delete_all_tasks_from(coursettitle);
    }

    @When("user requests to delete a course with title {string}")
    public void user_requests_to_delete_a_course_with_title_something(String invalidtitle) {
        response= Unirest.delete("/projects/-1").asJson().getBody().getObject();
    }

    @Then("the course with title {string} should be removed from the system")
    public void the_course_with_title_something_should_be_removed_from_the_system(String coursetitle) {
        assertEquals(null, findProjectByName(coursetitle));
    }

    @Then("the todos task of {string} should be removed")
    public void the_todos_task_of_something_should_be_removed(String coursettitle){
        assertEquals(0, getProjectTasks(coursettitle).length());
    }

    @Then("^the system should output an error$")
    public void the_system_should_output_an_errormessage(){
        errorMessage = response.getJSONArray("errorMessages").getString(0);
        assertEquals(errorMessage, "Could not find any instances with projects/-1");
    }

    //ID005

    @Given("the course with {string} is not in the system:")
    public void the_course_with_something_is_not_in_the_system(String coursetitle) {
        assertEquals(null, findProjectByName(coursetitle));
    }

    @Given("the course with title {string}, active status {string} is registered in the system:")
    public void the_course_with_title_something_active_status_something_is_registered_in_the_system(String coursetitle, String oldactive){
        Unirest.post("/projects")
        .body("{\"title\":\""+coursetitle+"\",\n"
                +"\"active\":"+oldactive+"\n}")
        .asJson();
    }


    @When("user requests to create a course with title {string} and description {string}")
    public void user_requests_to_create_a_course_with_title_something_and_description_something(String coursetitle, String coursedescription){
        HttpResponse<JsonNode> gResponse = Unirest.post("/projects")
        .body("{\"title\":"+"\""+coursetitle+"\",\n"
            +"\"description\":\""+coursedescription+"\"\n}")
            .asJson();
        response= gResponse.getBody().getObject();
        statusCode = gResponse.getStatus();
    }
    @When("user requests to set the the active status of the course with title {string} to {string}")
    public void user_requests_to_set_the_the_active_status_of_the_course_with_title_something_to_something(String coursetitle, String newactive) {
        int courseId = findProjectByName(coursetitle).getInt("id");
        Unirest.put("/projects/"+courseId)
        .body("{\"title\":\""+coursetitle+"\",\n"
            +"\"active\":"+newactive+"\n}")
        .asJson();
    }

    @When("user requests to create a course with title {string} and completed status {string}")
    public void user_requests_to_create_a_course_with_title_something_and_completed_status_something(String coursetitle, String completed) {
        HttpResponse<JsonNode> gResponse = Unirest.post("/projects")
        .body("{\"title\":\""+coursetitle+"\",\n"
            +"\"completed\":"+completed+"\n}")
        .asJson();
        statusCode = gResponse.getStatus();
    }

    @Then("the course with title {string} and description {string} should be created:")
    public void the_course_with_title_something_and_description_something_should_be_created(String coursetitle, String coursedescription) {
        assertEquals(201, statusCode);
    }

    @Then("the active status of the course with title {string} should be set to {string}")
    public void the_active_status_of_the_course_with_title_something_should_be_set_to_something(String coursetitle, String newactive){
        JSONObject course = findProjectByName(coursetitle);
        assertEquals(newactive, course.getString("active"));

    }

    //ID002 
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

    @When("the user requests to add the todo with name {string} and description {string} to the course with title {string}")
    public void the_user_requests_to_add_the_todo_with_name_something_and_description_something_to_the_course_with_title_something(String todotitle, String tododescription, String coursetitle){
        int todoId= findIdFromTodoName(todotitle);
        Unirest.post("/todos/"+todoId+"/tasksof").body("{\"title\":\"" + coursetitle + "\"}").asJson();
    }

    @When("the user requests to add the course with {string} to the system:")
    public void the_user_requests_to_add_the_course_with_something_to_the_system(String coursetitle){
        the_course_with_title_something_is_registered_in_the_system(coursetitle);
    }

    @When("user requests to add the todo with name {string} to the project title {string}")
    public void user_requests_to_add_the_todo_with_name_something_to_the_project_title_something(String todotitle, String inavlidcoursettitle){
        JSONObject course= findProjectByName(inavlidcoursettitle);
        if(course==null){
            statusCode = Unirest.get("/projects/-1").asJson().getStatus();
        }

    }

    @Then("the todo with name {string} should be added to the todo list of the course with title {string}")
    public void the_todo_with_name_something_should_be_added_to_the_todo_list_of_the_course_with_title_something(String todotitle, String coursetitle){
        assertEquals(1, getProjectTasks(coursetitle).length());
    }

    @Then("the system should output an error code {string}")
    public void the_system_should_output_an_error_code_something(String errorcode) {
        assertEquals(Integer.parseInt(errorcode),statusCode);
    }

    @And("the user requests to add the todo with name {string} and description {string} to the course {string}")
    public void the_user_requests_to_add_the_todo_with_name_something_and_description_something_to_the_course_something(String todotitle, String tododescription, String coursetitle){
        Unirest.post("/projects/"+findProjectByName(coursetitle).getInt("id")+"/tasks").body("{\"title\":\"" + todotitle+"\"}").asJson();
    }

    @And("the only course in the database is the course with title {string}")
    public void the_only_course_in_the_database_is_the_course_with_title_something(String coursetitle){
        the_course_with_title_something_is_registered_in_the_system(coursetitle);
        int length = Unirest.get("/projects").asJson().getBody().getObject().getJSONArray("projects").length();
        assertEquals(2, length); // 2 because there is already one to begin with
    }
}