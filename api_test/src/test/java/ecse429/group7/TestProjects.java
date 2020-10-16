package ecse429.group7;

import ecse429.group7.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.BeforeClass;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;


public class TestProjects extends BaseTest
{
    // Delete Todo to reset state of database
    public void deleteProjectById(HttpResponse<JsonNode> response)
    {
        int id = response.getBody().getObject().getInt("id");
        Unirest.delete("/projects/" + String.valueOf(id)).asJson();
    }

    //GET /projects
    @Test
    public void testGetProjectStatusCode()
    {
        assertGetStatusCode("/projects", STATUS_CODE_OK);
    }

    @Test
    public void testGetProjectsResponseSize()
    {
        HttpResponse<JsonNode> response = Unirest.get("/projects").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("projects").length(), 1);
    }

    @Test
    public void testGetProjectsResponseTitle()
    {
        HttpResponse<JsonNode> response = Unirest.get("/projects").asJson();
        String title = response.getBody().getObject().getJSONArray("projects").getJSONObject(0).getString("title");
        assertEquals("Office Work", title);
    }

    @Test
    public void testGetProjectsResponseCompleted()
    {
        HttpResponse<JsonNode> response = Unirest.get("/projects").asJson();
        String completed = response.getBody().getObject().getJSONArray("projects").getJSONObject(0).getString("completed");
        assertEquals("false", completed);
    }

    //HEAD /projects
    @Test
    public void testHeadTodos()
    {
        assertHeadStatusCode("/projects", STATUS_CODE_OK);
    }

    //POST /projects
    @Test
    public void testPostTodosJSONValidStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").body("{\n\"description\":\"Test Description\",\n    \"active\":true,\n    \"completed\":false,\n    \"title\":\"Test Title\"\n}")
        .asJson();
        
        //Delete object created to reset state of database
        deleteProjectById(response);
        
        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosJSONValidTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").body("{\n\"description\":\"Test Description\",\n    \"active\":true,\n    \"completed\":false,\n    \"title\":\"Test Title\"\n}")
        .asJson();
        
        //Delete object created to reset state of database
        deleteProjectById(response);
        
        assertEquals(response.getBody().getObject().getString("title"), "Test Title");
    }

    @Test
    public void testPostTodosJSONValidDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").body("{\n\"description\":\"Test Description\",\n    \"active\":true,\n    \"completed\":false,\n    \"title\":\"Test Title\"\n}")
        .asJson();
        
        //Delete object created to reset state of database
        deleteProjectById(response);
        
        assertEquals(response.getBody().getObject().getString("description"), "Test Description");
    }

    @Test
    public void testPostTodosJSONValidActive()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").body("{\n\"description\":\"Test Description\",\n    \"active\":true,\n    \"completed\":false,\n    \"title\":\"Test Title\"\n}")
        .asJson();
        
        //Delete object created to reset state of database
        deleteProjectById(response);
        
        assertEquals(response.getBody().getObject().getString("active"), "true");
    }

    @Test
    public void testPostTodosXMLValidStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").header("Content-Type", "application/xml")
        .body("<project>\n<description>Test Description</description>\n<active>True</active>\n<completed>False</completed>\n<title>Test Title</title>\n</project>\n").asJson();

        //Delete object created to reset state of database
        deleteProjectById(response);
        
        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosXMLValidTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").header("Content-Type", "application/xml")
        .body("<project>\n<description>Test Description</description>\n<active>True</active>\n<completed>False</completed>\n<title>Test Title</title>\n</project>\n").asJson();

        //Delete object created to reset state of database
        deleteProjectById(response);
        
        assertEquals(response.getBody().getObject().getString("title"), "Test Title");
    }

    @Test
    public void testPostTodosXMLValidDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").header("Content-Type", "application/xml")
        .body("<project>\n<description>Test Description</description>\n<active>True</active>\n<completed>False</completed>\n<title>Test Title</title>\n</project>\n").asJson();

        //Delete object created to reset state of database
        deleteProjectById(response);
        
        assertEquals(response.getBody().getObject().getString("description"), "Test Description");
    }

    @Test
    public void testPostTodosXMLValidActive()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").header("Content-Type", "application/xml")
        .body("<project>\n<description>Test Description</description>\n<active>True</active>\n<completed>False</completed>\n<title>Test Title</title>\n</project>\n").asJson();

        //Delete object created to reset state of database
        deleteProjectById(response);
        
        assertEquals(response.getBody().getObject().getString("active"), "true");
    }

    @Test
    public void testPostTodosJSONInvalidCompletionStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").body("{\n\"description\":\"Test Description\",\n    \"active\":true,\n    \"completed\":\"test\",\n    \"title\":\"Test Title\"\n}")
        .asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostTodosJSONInvalidCompletionErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").body("{\n\"description\":\"Test Description\",\n    \"active\":true,\n    \"completed\":\"test\",\n    \"title\":\"Test Title\"\n}")
        .asJson();
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Failed Validation: completed should be BOOLEAN");
    }

    @Test
    public void testPostTodosXMLInvalidCompletionStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").header("Content-Type", "application/xml")
        .body("<project>\n<description>Test Description</description>\n<active>True</active>\n<completed>Test</completed>\n<title>Test Title</title>\n</project>\n").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostTodosXMLInvalidCompletionErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects").header("Content-Type", "application/xml")
        .body("<project>\n<description>Test Description</description>\n<active>True</active>\n<completed>Test</completed>\n<title>Test Title</title>\n</project>\n").asJson();
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Failed Validation: completed should be BOOLEAN");
    }

}