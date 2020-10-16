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
    public void testHeadProjects()
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

      //GET /projects/:id
      @Test
      public void testGetProjectIdStatusCode()
      {
          assertGetStatusCode("/projects/1", STATUS_CODE_OK);
      }
  
      @Test
      public void testGetProjectsIdTitle()
      {
          HttpResponse<JsonNode> response = Unirest.get("/projects/1").asJson();
          assertEquals(response.getBody().getObject().getJSONArray("projects").getJSONObject(0).getString("title"), "Office Work");
      }

    //HEAD /projects/:id
    @Test
    public void testHeadIdProjects()
    {
        assertHeadStatusCode("/projects/1", STATUS_CODE_OK);
    }

    //POST /projects/:id
    @Test
    public void testPostProjectsIdInvalidCompletionJSONStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\":\"New Title\",\n    \"completed\":\"true\"\n}")
        .asJson();
      
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostProjectsIdInvalidCompletionJSONErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\":\"New Title\",\n    \"completed\":\"true\"\n}")
        .asJson();   

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Failed Validation: completed should be BOOLEAN");
    }

    @Test
    public void testPostProjectsIdJSONStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\":\"New Title\",\n    \"completed\":true\n}")
        .asJson();
      
        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPostProjectsIdJSONTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\":\"New Title\",\n    \"completed\":true\n}")
        .asJson();
        
        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getBody().getObject().getString("title"), "New Title");
    }

    @Test
    public void testPostProjectsIdJSONCompletion()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\":\"New Title\",\n    \"completed\":true\n}")
        .asJson();
        
        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getBody().getObject().getString("completed"), "true");
    }

    @Test
    public void testPostProjectsIdXMLStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/xml")
        .body("<project><title>New Title</title><completed>true</completed></project>\n")
        .asJson();
      
        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPostProjectsIdXMLTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/xml")
        .body("<project><title>New Title</title><completed>true</completed></project>\n")
        .asJson();
        
        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getBody().getObject().getString("title"), "New Title");
    }

    @Test
    public void testPostProjectsIdXMLCompletion()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/xml")
        .body("<project><title>New Title</title><completed>true</completed></project>\n")
        .asJson();
        
        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getBody().getObject().getString("completed"), "true");
    }

    //PUT /projects/:id
    @Test
    public void testPutProjectsWithTasksJSONStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("   \n   {\"description\":\"test new description\",\n   \"tasks\": [\n       {\n           \"id\": 1\n       },\n       {\n           \"id\": 2\n       }\n   ]}\n")
        .asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPutProjectsWithTasksJSONErrorMessages()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("   \n   {\"description\":\"test new description\",\n   \"tasks\": [\n       {\n           \"id\": 1\n       },\n       {\n           \"id\": 2\n       }\n   ]}\n")
        .asJson();
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Could not find field: tasks");
    }

    @Test
    public void testPutProjectsJSONStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("   \n   {\"description\":\"test new description\"}\n")
        .asJson();

        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPutProjectsJSONDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("   \n   {\"description\":\"test new description\"}\n")
        .asJson();

        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getBody().getObject().getString("description"), "test new description");
    }

    @Test
    public void testPutProjectsXMLStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/xml")
        .body("<project><description>test new description</description></project>")
        .asJson();

        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPutProjectsXMLDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/projects/1")
        .header("Content-Type", "application/xml")
        .body("<project><description>test new description</description></project>")
        .asJson();

        // Reset to previous state
        Unirest.post("/projects/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Office Work\",\n    \"completed\": false,\n    \"active\": false,\n    \"description\": \"\"   \n}\n").asString();
        
        assertEquals(response.getBody().getObject().getString("description"), "test new description");
    }
}