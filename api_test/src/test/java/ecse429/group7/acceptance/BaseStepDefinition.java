package ecse429.group7.acceptance;

import ecse429.group7.BaseTest;

import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

import static org.junit.Assert.*;

import java.util.List;

public class BaseStepDefinition extends BaseTest {
    static String errorMessage;
    static int statusCode;
    static JSONObject originalValue;
    static JSONObject response;
    static JSONObject originalTodoList;
    static JSONArray taskList;
    static int counter;

    public static JSONObject findTodoByName(String todo_name) {
        JSONObject response = Unirest.get("/todos").asJson().getBody().getObject();
        for (Object todo : response.getJSONArray("todos")) {
            JSONObject t = (JSONObject) todo;
            if (t.getString("title").equals(todo_name))
                return t;
        }
        return null;
    }

    public static JSONObject findProjectByName(String projectName) {
        JSONObject response = Unirest.get("/projects").asJson().getBody().getObject();
        for (Object proj : response.getJSONArray("projects")) {
            JSONObject project = (JSONObject) proj;
            if (project.getString("title").equals(projectName)) {
                return project;
            }
         }
        return null;
    }

    public static JSONArray getProjectTasks(String projectName) {
        JSONObject proj = findProjectByName(projectName);
        if (proj == null) return null;
        int id = proj.getInt("id");
        return Unirest.get("/projects/" + id + "/tasks")
                .asJson().getBody().getObject().getJSONArray("todos");
    }

    public static int findIdFromTodoName(String todo_name) {
        JSONObject todo = findTodoByName(todo_name);
        if (todo == null) return -1;
        return todo.getInt("id");
    }

    public static int findIdFromTodoCategoryName(String category_name, String todo_name) {
        JSONObject response = Unirest.get("/todos").asJson().getBody().getObject();
        int id = -1;

        for (Object todo : response.getJSONArray("todos")) {
            JSONObject t = (JSONObject) todo;
            if (t.getString("title").equals(todo_name)) {
                int todo_id = t.getInt("id");
                JSONArray response_cat = Unirest.get("/todos/" + todo_id + "/categories").asJson().getBody().getObject()
                        .getJSONArray("categories");
                for (Object cat : response_cat) {
                    JSONObject c = (JSONObject) cat;
                    if (c.getString("title").equals(category_name)) {
                        id = c.getInt("id");
                        break;
                    }
                }
            }
        }

        return id;
    }

    public JSONObject addTodoByRow(List<String> columns) {
        String title = "\"title\":\"" + columns.get(0) + "\"";
        String doneStatus = "\"doneStatus\":" + columns.get(1);
        String description = "\"description\":\"" + columns.get(2) + "\"";
        JSONObject todoObj = Unirest.post("/todos")
                .body("{\n" + title + ",\n" + doneStatus + ",\n" + description + "\n}").asJson().getBody().getObject();
        if (columns.size() == 4) {
            request_priority_for_todo(columns.get(0), columns.get(3));
        }
        return todoObj;
    }
    
    public static void assertDoneStatusEquals(JSONObject todo, boolean val) {
        assertNotNull(todo);
        assertTrue(todo.getString("doneStatus").equalsIgnoreCase(val + ""));
    }


    public static void projecteHasOutstandingTasks(String projectTitle, boolean checkIncomplete) {
        JSONArray projects = getProjectTasks(projectTitle);
        for (Object o : projects) {
            int id = ((JSONObject) o).getInt("id");
            JSONObject todo = (JSONObject) Unirest.get("/todos/" + id).asJson().getBody().getObject()
                    .getJSONArray("todos").get(0);
            // NOTE This assumptions makes it less reusable
            if (!checkIncomplete || todo.getString("doneStatus").equalsIgnoreCase("false")) {
                return;
            }
        }
        fail();
    }
    
    public void request_priority_for_todo(String todotitle, String prioritytoassign) {
        // Find ID of Task todo_title
        int id = findIdFromTodoName(todotitle.replace("\"", ""));

        HttpResponse<JsonNode> response = Unirest.post("/todos/" + id +"/categories")
                .body("{\n\"title\":\"" + prioritytoassign.replace("\"", "") + "\"\n}\n").asJson();
        
        statusCode = response.getStatus();
        if(statusCode != 200 && statusCode != 201) {
            errorMessage = response.getBody().getObject().getJSONArray("errorMessages").getString(0);
        }
    }
}
