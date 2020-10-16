package ecse429.group7;

import ecse429.group7.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.BeforeClass;

import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;


public class TestTodos extends BaseTest
{
    // Delete Todo to reset state of database
    public void deleteTodoById(HttpResponse<JsonNode> response)
    {
        int id = response.getBody().getObject().getInt("id");
        Unirest.delete("/todos/" + String.valueOf(id)).asJson();
    }

    // GET /todos
    @Test
    public void testGetTodosStatusCode()
    {
        assertGetStatusCode("/todos", STATUS_CODE_OK);
    }

    @Test
    public void testGetTodosResponseSize()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("todos").length(), 2);
    }

    @Test
    public void testGetTodosResponseFirstTitle()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos").asJson();
        String title = response.getBody().getObject().getJSONArray("todos").getJSONObject(0).getString("title");
        assertTrue(title.equals("file paperwork") || title.equals("scan paperwork"));
    }

    @Test
    public void testGetTodosResponseSecondDoneStatus()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("todos").getJSONObject(1).getString("doneStatus"), "false");
    }

    @Test
    public void testGetTodosResponseDescriptionEmpty()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("todos").getJSONObject(1).getString("description"), "");
    }

    // GET /todos/:id
    @Test
    public void testGetTodosInvalidTodoStatusCode()
    {
        assertGetStatusCode("/todos/3", STATUS_CODE_NOT_FOUND);
    }

    @Test
    public void testGetTodosInvalidTodoErrorMessage()
    {
        assertGetErrorMessage("/todos/3", "Could not find an instance with todos/3", 0);
    }

    @Test
    public void testGetTodosValidStatusCode()
    {
        assertGetStatusCode("/todos/2", STATUS_CODE_OK);
    }

    @Test
    public void testGetTodosValidBodyTitle()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos/2").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("todos").getJSONObject(0).getString("title"), "file paperwork");
    }

    @Test
    public void testGetTodosValidTaskOf()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos/2").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("todos").getJSONObject(0)
            .getJSONArray("task-of").getJSONObject(0).getInt("id"), 1);
    }

    // HEAD /todos
    @Test
    public void testHeadTodos()
    {
        assertHeadStatusCode("/todos", STATUS_CODE_METHOD_NOT_ALLOWED);
    }

    // POST /todos
    public  void testPostTodosInvalidStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").asJson();
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostTodosErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "title : field is mandatory");
    }

    @Test
    public void testPostTodosJSONValidStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").body("{\"title\":\"NewTodo\"}").asJson();
        
        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosJSONValidTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").body("{\"title\":\"NewTodo\"}").asJson();
        
        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getBody().getObject().getString("title"), "NewTodo");
    }

    @Test
    public void testPostTodosXMLValidStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .body("<todo><title>NewTodo</title></todo>").asJson();

        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosXMLValidTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .body("<todo><title>NewTodo</title></todo>").asJson();

        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getBody().getObject().getString("title"), "NewTodo");
    }

    @Test
    public void testPostTodosWithIdJSONStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/json")
        .body("{\n    \"title\":\"Test101\",\n    \"id\":12\n}").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostTodosWithIdJSONErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/json")
        .body("{\n    \"title\":\"Test101\",\n    \"id\":12\n}").asJson();
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Invalid Creation: Failed Validation: Not allowed to create with id");
    }

    @Test
    public void testPostTodosWithIdXMLStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .body("<todo><title>Test101</title><id>12</id></todo>").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }


    @Test
    public void testPostTodosJSONValidStatusCodeWithDoneStatus()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").body("{\"title\":\"NewTodo\",\"doneStatus\":true}").asJson();
        
        //Delete object created to reset state of database
        deleteTodoById(response);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosJSONValidDoneStatus()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").body("{\"title\":\"NewTodo\",\"doneStatus\":true}").asJson();
        
        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getBody().getObject().getString("doneStatus"), "true");
    }

    @Test
    public void testPostTodosXMLValidStatusCodeWithDoneStatus()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .body("<todo><title>NewTodo</title><doneStatus>true</doneStatus></todo>").asJson();

        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosXMLValidDoneStatus()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .body("<todo><title>NewTodo</title><doneStatus>true</doneStatus></todo>").asJson();

        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getBody().getObject().getString("doneStatus"), "true");
    }

    // Test description of 10.0
    @Test
    public void testPostTodosJSONValidStatusCodeWithDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos")
        .body("{\"title\":\"NewTodo\",\"doneStatus\":true,\"description\":10.0}").asJson();
        
        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosJSONValidDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos")
        .body("{\"title\":\"NewTodo\",\"doneStatus\":true,\"description\":10.0}").asJson();

        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getBody().getObject().getString("description"), "10.0");
    }

    @Test
    public void testPostTodosXMLValidStatusCodeWithDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .body("<todo><title>NewTodo</title><doneStatus>true</doneStatus><description>10</description></todo>").asJson();

        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosXMLValidDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .body("<todo><title>NewTodo</title><doneStatus>true</doneStatus><description>10</description></todo>").asJson();

        //Delete object created to reset state of database
        deleteTodoById(response);
        
        assertEquals(response.getBody().getObject().getString("description"), "10.0");
    }

    // Test invented field
    @Test
    public void testPostTodosJSONStatusCodeInventedField()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Test429\",\n    \"description\": false,\n    \"inventedField\": \"test\"\n}\n").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    @Test
    public void testPostTodosJSONInventedFieldErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/json")
        .body("{\n    \"title\": \"Test429\",\n    \"description\": false,\n    \"inventedField\": \"test\"\n}\n").asJson();
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Could not find field: inventedField on Entity todo");
    }

    @Test
    public void testPostTodosXMLStatusCodeInventedField()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .header("Content-Type", "application/xml")
        .body("<todo>\n    <title>Test429</title><description>false</description><inventedField>test</inventedField>\n</todo>\n").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    // PUT /todos/:id
    @Test
    public void testPutTodosJSONStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.put("/todos/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\":\"New Title\",\n    \"description\":\"Test description\"\n}")
        .asJson();
      
        // Reset to previous state
        Unirest.put("/todos/1")
        .header("Content-Type", "application/json")
        .body("{\n\"doneStatus\": false,\n\"description\": \"\",\n\"title\": \"scan paperwork\",\n \"categories\": [\n{\n\"id\": \"1\"\n}\n],\n\"task-of\": [\n{\n\"id\": \"1\"\n}\n]\n}")
        .asString();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPutTodosJSONTitle()
    {
        HttpResponse<JsonNode> response = Unirest.put("/todos/1")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\":\"New Title\",\n    \"description\":\"Test description\"\n}")
        .asJson();
      
        // Reset to previous state
        Unirest.put("/todos/1")
        .header("Content-Type", "application/json")
        .body("{\n\"doneStatus\": false,\n\"description\": \"\",\n\"title\": \"scan paperwork\",\n \"categories\": [\n{\n\"id\": \"1\"\n}\n],\n\"task-of\": [\n{\n\"id\": \"1\"\n}\n]\n}")
        .asString();

        assertEquals(response.getBody().getObject().getString("title"), "New Title");
    }

    @Test
    public void testPutTodosXMLStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.put("/todos/1")
        .header("Content-Type", "application/xml")
        .body("<todo><title>New Title</title><description>Test description</description></todo>")
        .asJson();
      
        // Reset to previous state
        Unirest.put("/todos/1")
        .header("Content-Type", "application/json")
        .body("{\n\"doneStatus\": false,\n\"description\": \"\",\n\"title\": \"scan paperwork\",\n \"categories\": [\n{\n\"id\": \"1\"\n}\n],\n\"task-of\": [\n{\n\"id\": \"1\"\n}\n]\n}")
        .asString();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testPutTodosXMLTitle()
    {
        HttpResponse<JsonNode> response = Unirest.put("/todos/1")
        .header("Content-Type", "application/xml")
        .body("<todo><title>New Title</title><description>Test description</description></todo>")
        .asJson();
      
        // Reset to previous state
        Unirest.put("/todos/1")
        .header("Content-Type", "application/json")
        .body("{\n\"doneStatus\": false,\n\"description\": \"\",\n\"title\": \"scan paperwork\",\n \"categories\": [\n{\n\"id\": \"1\"\n}\n],\n\"task-of\": [\n{\n\"id\": \"1\"\n}\n]\n}")
        .asString();

        assertEquals(response.getBody().getObject().getString("title"), "New Title");
    }

    // DELETE /todos/:id
    @Test
    public void testDeleteTodosStatusCode()
    {
        // create todo to delete
        HttpResponse<JsonNode> response = Unirest.post("/todos")
        .header("Content-Type", "application/json")
        .body("{\n    \"title\":\"New Title\",\n    \"description\":\"Test description\"\n}")
        .asJson();
        
        int id = response.getBody().getObject().getInt("id");

        response = Unirest.delete("/todos/" + String.valueOf(id)).header("Content-Type", "application/json")
        .asJson();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

        // DELETE /todos/:id
        @Test
        public void testDeleteTodosVerifyDeletion()
        {
            // create todo to delete
            HttpResponse<JsonNode> response = Unirest.post("/todos")
            .header("Content-Type", "application/json")
            .body("{\n    \"title\":\"New Title\",\n    \"description\":\"Test description\"\n}")
            .asJson();
            
            int id = response.getBody().getObject().getInt("id");

            response = Unirest.get("/todos").asJson();
            int original_size = response.getBody().getObject().getJSONArray("todos").length();
    
            response = Unirest.delete("/todos/" + String.valueOf(id)).header("Content-Type", "application/json")
            .asJson();

            response = Unirest.get("/todos").asJson();
            int new_size = response.getBody().getObject().getJSONArray("todos").length();

    
            assertEquals(original_size - new_size, 1);
        }

}

