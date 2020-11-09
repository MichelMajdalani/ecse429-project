package ecse429.group7;

import kong.unirest.UnirestException;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;


import static org.junit.Assert.assertEquals;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class BaseTest {
    public static final String BASE_URL = "http://localhost:4567";
    protected static final int STATUS_CODE_OK = 200;
    protected static final int STATUS_CODE_CREATED = 201;
    protected static final int STATUS_CODE_BAD_REQUEST = 400;
    protected static final int STATUS_CODE_NOT_FOUND = 404;
    private static Process serverProcess;

    @BeforeClass
    public static void setupForAllTests() {
        Unirest.config().defaultBaseUrl(BASE_URL);
        startServer();
    }

    @AfterClass
    public static void tearDownAllTests() {
        stopServer();
    }

    public static void startServer() {
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", "../runTodoManagerRestAPI-1.5.5.jar");
            if (serverProcess != null) {
                serverProcess.destroy();
            }
            serverProcess = pb.start();
            final InputStream is = serverProcess.getInputStream();
            final BufferedReader output = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = output.readLine();
                if (line != null && line.contains("Running on 4567")) {
                    waitUntilOnline();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitUntilOnline() {
        int tries = 0;
        while (!isOnline()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {}
            tries++;
            if (tries > 100) {
                startServer();
                tries = 0;
            }
        }
    }

    public static boolean isOnline() {
        try {
            int status = Unirest.get("/").asString().getStatus();
            return status == 200;
        } catch (UnirestException ignored) { }
        return false;
    }

    public static void stopServer() {
        serverProcess.destroy();
    }

    public static void assertGetStatusCode(String url, int status_code) {
        HttpResponse<JsonNode> response = Unirest.get(url).asJson();
        assertEquals(response.getStatus(), status_code);
    }

    public static void assertHeadStatusCode(String url, int status_code) {
        HttpResponse<JsonNode> response = Unirest.head(url).asJson();
        assertEquals(response.getStatus(), status_code);
    }

    public static void assertGetErrorMessage(String url, String expected_message, int index) {
        HttpResponse<JsonNode> response = Unirest.get(url).asJson();
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(index), expected_message);
    }

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
                JSONArray response_cat = Unirest.get("/todos/" + todo_id + "/categories").asJson()
                        .getBody().getObject().getJSONArray("categories");
                for (Object cat : response_cat) {
                    JSONObject c = (JSONObject) cat;
                    System.out.println(c.getString("title"));
                    if (c.getString("title").equals(category_name)) {
                        id = c.getInt("id");
                        break;
                    }
                }
            }
        }

        return id;
    }
}
