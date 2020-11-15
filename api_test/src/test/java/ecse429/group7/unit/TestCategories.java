package ecse429.group7.unit;

import ecse429.group7.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;

public class TestCategories extends BaseTest {
    // Get initial state of categories
    public String getFirstCategoryState() {
        return "{\n    \"title\": \"Office\",\n  \"description\": \"\"   \n}\n";
    }

    public String getSecondCategoryState() {
        return "{\n    \"title\": \"Home\",\n  \"description\": \"\"   \n}\n";
    }

    public void resetFirstInitialState() {
        // Reset to previous state
        Unirest.post("/categories/1").header("Content-Type", "application/json").body(getFirstCategoryState())
                .asString();
    }

    // Delete Todo to reset state of database
    public void deleteCategoryById(HttpResponse<JsonNode> response) {
        int id = response.getBody().getObject().getInt("id");
        Unirest.delete("/categories/" + id).asJson();
    }

    // Delete category to reset state of database (must delete both association and
    // new object)
    public void deleteTodosOfCategoryById(HttpResponse<JsonNode> response, int id) {
        int created_id = response.getBody().getObject().getInt("id");
        Unirest.delete("/categories/" + id + "/todos/" + created_id).asJson();
        Unirest.delete("/todos/" + created_id).asJson();
    }

    // Delete projects to reset state of database (must delete both association and
    // new object)
    public void deleteProjectsOfCategoryById(HttpResponse<JsonNode> response, int id) {
        int created_id = response.getBody().getObject().getInt("id");
        Unirest.delete("/categories/" + id + "/projects/" + created_id).asJson();
        Unirest.delete("/projects/" + created_id).asJson();
    }

    // GET /categories
    @Test
    public void testGetCategoryStatusCode() {
        assertGetStatusCode("/categories", STATUS_CODE_OK);
    }

    @Test
    public void testGetCategoriesResponseSize() {
        HttpResponse<JsonNode> response = Unirest.get("/categories").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("categories").length(), 2);
    }

    @Test
    public void testGetCategoriesResponseTitle() {
        HttpResponse<JsonNode> response = Unirest.get("/categories").asJson();
        String title = response.getBody().getObject().getJSONArray("categories").getJSONObject(0).getString("title");
        assertTrue(title.equals("Home") || title.equals("Office"));
    }

    @Test
    public void testGetCategoriesDescriptions() {
        HttpResponse<JsonNode> response = Unirest.get("/categories").asJson();
        String description = response.getBody().getObject().getJSONArray("categories").getJSONObject(0)
                .getString("description");
        assertEquals(description, "");
    }

    // HEAD /categories
    @Test
    public void testHeadCategories() {
        assertHeadStatusCode("/categories", STATUS_CODE_OK);
    }

    // POST /categories
    @Test
    public void testPostTodosJSONValidStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories")
                .body("{\n\"description\":\"Test Description\",\n  \"title\":\"Test Title\"\n}").asJson();

        // Delete object created to reset state of database
        deleteCategoryById(response);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosJSONValidTitle() {
        HttpResponse<JsonNode> response = Unirest.post("/categories")
                .body("{\n\"description\":\"Test Description\",\n   \"title\":\"Test Title\"\n}").asJson();

        // Delete object created to reset state of database
        deleteCategoryById(response);

        assertEquals(response.getBody().getObject().getString("title"), "Test Title");
    }

    @Test
    public void testPostTodosJSONValidDescription() {
        HttpResponse<JsonNode> response = Unirest.post("/categories")
                .body("{\n\"description\":\"Test Description\",\n   \"title\":\"Test Title\"\n}").asJson();

        // Delete object created to reset state of database
        deleteCategoryById(response);

        assertEquals(response.getBody().getObject().getString("description"), "Test Description");
    }

    @Test
    public void testPostTodosXMLValidStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/xml").body(
                "<category>\n<description>Test Description</description>\n<title>Test Title</title>\n</category>\n")
                .asJson();

        // Delete object created to reset state of database
        deleteCategoryById(response);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosXMLValidTitle() {
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/xml").body(
                "<category>\n<description>Test Description</description>\n<title>Test Title</title>\n</category>\n")
                .asJson();

        // Delete object created to reset state of database
        deleteCategoryById(response);

        assertEquals(response.getBody().getObject().getString("title"), "Test Title");
    }

    @Test
    public void testPostTodosXMLValidDescription() {
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/xml").body(
                "<category>\n<description>Test Description</description>\n<title>Test Title</title>\n</category>\n")
                .asJson();

        // Delete object created to reset state of database
        deleteCategoryById(response);

        assertEquals(response.getBody().getObject().getString("description"), "Test Description");
    }

    @Test
    public void testPostTodosJSONInvalidFieldStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories").body(
                "{\n\"description\":\"Test Description\",\n  \"completed\":\"test\",\n    \"title\":\"Test Title\"\n}")
                .asJson();

        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostTodosJSONInvalidFieldErrorMessage() {
        HttpResponse<JsonNode> response = Unirest.post("/categories")
                .body("{\n\"description\":\"Test Description\",\n  \"name\":\"test\",\n    \"title\":\"Test Title\"\n}")
                .asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0),
                "Could not find field: name");
    }

    @Test
    public void testPostTodosXMLInvalidFieldStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/xml").body(
                "<category>\n<description>Test Description</description>\n<completed>Test</completed>\n<title>Test Title</title>\n</category>\n")
                .asJson();

        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostTodosXMLInvalidCompletionErrorMessage() {
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/xml").body(
                "<category>\n<description>Test Description</description>\n<active>True</active>\n<name>Test</name>\n<title>Test Title</title>\n</category>\n")
                .asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0),
                "Could not find field: name");
    }

    // GET /categories/:id
    @Test
    public void testGetCategoryIdStatusCode() {
        assertGetStatusCode("/categories/1", STATUS_CODE_OK);
    }

    @Test
    public void testGetCategoriesIdTitle() {
        HttpResponse<JsonNode> response = Unirest.get("/categories/1").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("categories").getJSONObject(0).getString("title"),
                "Office");
    }

    // HEAD /categories/:id
    @Test
    public void testHeadIdCategories() {
        assertHeadStatusCode("/categories/1", STATUS_CODE_OK);
    }

    // POST /categories/:id
    @Test
    public void testPostCategoriesIdInvalidFieldJSONStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"New Title\",\n    \"name\":\"true\"\n}").asJson();

        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostCategoriesIdInvalidFieldJSONErrorMessage() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"New Title\",\n    \"name\":\"true\"\n}").asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0),
                "Could not find field: name");
    }

    @Test
    public void testPostCategoriesIdJSONStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"New Title\",\n    \"description\":\"New Description Title\"\n}").asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPostCategoriesIdJSONTitle() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"New Title\",\n    \"description\":\"New Description Title\"\n}").asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getBody().getObject().getString("title"), "New Title");
    }

    @Test
    public void testPostCategoriesIdJSONDescriptiontion() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"New Title\",\n    \"description\":\"New Description Title\"\n}").asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getBody().getObject().getString("description"), "New Description Title");
    }

    @Test
    public void testPostCategoriesIdXMLStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/xml")
                .body("<category><title>New Title</title><description>New Description Title</description></category>\n")
                .asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPostCategoriesIdXMLTitle() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/xml")
                .body("<category><title>New Title</title><description>New Description Title</description></category>\n")
                .asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getBody().getObject().getString("title"), "New Title");
    }

    @Test
    public void testPostCategoriesIdXMLDescription() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/xml")
                .body("<category><title>New Title</title><description>New Description Title</description></category>\n")
                .asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getBody().getObject().getString("description"), "New Description Title");
    }

    // PUT /categories/:id
    @Test
    public void testPutCategoriesWithProjectsJSONStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json").body(
                "   \n   {\"description\":\"test new description\",\n   \"projects\": [\n       {\n           \"id\": 1\n       },\n       {\n           \"id\": 2\n       }\n   ]}\n")
                .asJson();

        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPutCategoriesWithProjectsJSONErrorMessages() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json").body(
                "   \n   {\"description\":\"test new description\",\n   \"projects\": [\n       {\n           \"id\": 1\n       },\n       {\n           \"id\": 2\n       }\n   ]}\n")
                .asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0),
                "Could not find field: projects");
    }

    @Test
    public void testPutCategoriesJSONStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json")
                .body("   \n   {\"description\":\"test new description\"}\n").asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPutCategoriesJSONDescription() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/json")
                .body("   \n   {\"description\":\"test new description\"}\n").asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getBody().getObject().getString("description"), "test new description");
    }

    @Test
    public void testPutCategoriesXMLStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/xml")
                .body("<category><description>test new description</description></category>").asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPutCategoriesXMLDescription() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1").header("Content-Type", "application/xml")
                .body("<category><description>test new description</description></category>").asJson();

        // Reset to previous state
        resetFirstInitialState();

        assertEquals(response.getBody().getObject().getString("description"), "test new description");
    }

    // DELETE /categories/:id
    @Test
    public void testDeleteCategoriesStatusCode() {
        // create project to delete
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"New Title\",\n    \"description\":\"Test description\"\n}").asJson();

        int id = response.getBody().getObject().getInt("id");

        response = Unirest.delete("/categories/" + id).header("Content-Type", "application/json")
                .asJson();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testDeleteCategoriesVerifyDeletion() {
        // create todo to delete
        HttpResponse<JsonNode> response = Unirest.post("/categories").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"New Title\",\n    \"description\":\"Test description\"\n}").asJson();

        int id = response.getBody().getObject().getInt("id");

        response = Unirest.get("/categories").asJson();
        int original_size = response.getBody().getObject().getJSONArray("categories").length();

        Unirest.delete("/categories/" + id).header("Content-Type", "application/json").asJson();

        response = Unirest.get("/categories").asJson();
        int new_size = response.getBody().getObject().getJSONArray("categories").length();

        assertEquals(original_size - new_size, 1);
    }

    // GET /categories/:id/todos
    @Test
    public void testGetCategoryCategoriesStatusCode() {
        assertGetStatusCode("/categories/1/todos", STATUS_CODE_OK);
    }

    @Test
    public void testGetCategoriesCategoriesResponseSize() {
        HttpResponse<JsonNode> response = Unirest.get("/categories/1/todos").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("todos").length(), 0);
    }

    // HEAD /categories/:id/todos
    @Test
    public void testHeadCategoriesCategories() {
        assertHeadStatusCode("/categories/1/todos", STATUS_CODE_OK);
    }

    // POST /categories/:id/todos
    @Test
    public void testPostCategoryCategoriesJSONStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"Test Title\"}").asJson();

        // reset database private state
        deleteTodosOfCategoryById(response, 1);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostCategoryCategoriesJSONNewTitle() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"Test Title\"}").asJson();

        // reset database private state
        deleteTodosOfCategoryById(response, 1);

        assertEquals(response.getBody().getObject().getString("title"), "Test Title");
    }

    @Test
    public void testPostCategoryCategoriesJSONInavlidCompletionStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"\"}").asJson();

        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostCategoryCategoriesJSONInvalidCompletionErrorMessage() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"\"}").asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0),
                "Failed Validation: title : can not be empty");

    }

    @Test
    public void testPostCategoryCategoriesJSONMalformedStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"Test Title}").asJson();

        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostCategoryCategoriesJSONMalformedErrorMessage() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/json")
                .body("{\n    \"title\":\"Test Title}").asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0),
                "com.google.gson.stream.MalformedJsonException: Unterminated string at line 2 column 25 path $.");
    }

    @Test
    public void testPostCategoryCategoriesXMLStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title>Test Title</title></category>").asJson();

        // reset database private state
        deleteTodosOfCategoryById(response, 1);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostCategoryCategoriesXMLNewTitle() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title>Test Title</title></category>").asJson();

        // reset database private state
        deleteTodosOfCategoryById(response, 1);

        assertEquals(response.getBody().getObject().getString("title"), "Test Title");
    }

    @Test
    public void testPostCategoryCategoriesXMLInvalidCompletionStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title></title></category>").asJson();

        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);

    }

    @Test
    public void testPostCategoryCategoriesXMLInvalidCompletionErrorMessage() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title></title></category>").asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0),
                "Failed Validation: title : can not be empty");

    }

    @Test
    public void testPostCategoryCategoriesXMLMalformedStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title>Test Title</title><category>").asJson();

        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);

    }

    @Test
    public void testPostCategoryCategoriesXMLMalformedErrorMessage() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title>Test Title</title><category>").asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0),
                "Unclosed tag category at 46 [character 47 line 1]");
    }

    // DELETE /categories/:id/todos/:id
    @Test
    public void testDeleteCategoryCategoriesStatusCode() {
        // create project category to delete
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title>Test Title</title></category>").asJson();
        int id = response.getBody().getObject().getInt("id");

        response = Unirest.delete("/categories/1/todos/" + id)
                .header("Content-Type", "application/json").asJson();

        // Must also delete todos
        Unirest.delete("/todos/" + id).asJson();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testDeleteTwiceCategoryCategoriesStatusCode() {
        // create project category to delete
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title>Test Title</title></category>").asJson();
        int id = response.getBody().getObject().getInt("id");
        Unirest.delete("/categories/1/todos/" + id).header("Content-Type", "application/json").asJson();

        response = Unirest.delete("/categories/1/todos/" + id)
                .header("Content-Type", "application/json").asJson();

        // Must also delete todos
        Unirest.delete("/todos/" + id).asJson();

        assertEquals(response.getStatus(), STATUS_CODE_NOT_FOUND);
    }

    @Test
    public void testDeleteCategoryCategoriesVerifyDeletion() {
        // create project category to delete
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/todos").header("Content-Type", "application/xml")
                .body("<category><title>Test Title</title></category>").asJson();

        int id = response.getBody().getObject().getInt("id");

        response = Unirest.get("/categories/1/todos").asJson();
        int original_size = response.getBody().getObject().getJSONArray("todos").length();

        Unirest.delete("/categories/1/todos/" + id).header("Content-Type", "application/json").asJson();

        // Must also delete todos
        Unirest.delete("/todos/" + id).asJson();

        response = Unirest.get("/categories/1/todos").asJson();
        int new_size = response.getBody().getObject().getJSONArray("todos").length();

        assertEquals(original_size - new_size, 1);
    }

    // GET /categories/:id/projects
    @Test
    public void testGetCategoryProjectsStatusCode() {
        assertGetStatusCode("/categories/1/projects", STATUS_CODE_OK);
    }

    @Test
    public void testGetCategoryProjectsResponseSize() {
        HttpResponse<JsonNode> response = Unirest.get("/categories/1/projects").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("projects").length(), 0);
    }

    // HEAD /categories/:id/projects
    @Test
    public void testHeadCategoriesProjects() {
        assertHeadStatusCode("/categories/1/projects", STATUS_CODE_OK);
    }

    // POST /categories/:id/projects
    @Test
    public void testPostCategoryProjectsJSONStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/projects")
                .header("Content-Type", "application/json")
                .body("{\n   \"title\":\"New Task\",\n   \"completed\":true,\n   \"description\":\"Testing\"\n}\n")
                .asJson();

        // reset database private state
        deleteProjectsOfCategoryById(response, 1);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostCategoryTaskJSONNewTitle() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/projects")
                .header("Content-Type", "application/json")
                .body("{\n   \"title\":\"New Task\",\n   \"completed\":true,\n   \"description\":\"Testing\"\n}\n")
                .asJson();

        // reset database private state
        deleteProjectsOfCategoryById(response, 1);

        assertEquals(response.getBody().getObject().getString("title"), "New Task");
    }

    @Test
    public void testPostCategoryProjectsXMLStatusCode() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/projects")
                .header("Content-Type", "application/xml")
                .body("<project>\n<title>New Task</title>\n<completed>true</completed>\n<description>Testing</description>\n</project>\n")
                .asJson();

        // reset database private state
        deleteProjectsOfCategoryById(response, 1);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostCategoryTaskXMLNewTitle() {
        HttpResponse<JsonNode> response = Unirest.post("/categories/1/projects")
                .header("Content-Type", "application/xml")
                .body("<project>\n<title>New Task</title>\n<completed>true</completed>\n<description>Testing</description>\n</project>\n")
                .asJson();

        // reset database private state
        deleteProjectsOfCategoryById(response, 1);

        assertEquals(response.getBody().getObject().getString("title"), "New Task");
    }

}