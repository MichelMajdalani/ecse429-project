package ecse429.group7;

import ecse429.group7.BaseTest;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;


public class TestTodos extends BaseTest
{
    // Delete Todo to reset state of database
    public void deleteTodoById(HttpResponse<JsonNode> response)
    {
        int id = response.getBody().getObject().getInt("id");
        Unirest.delete("/todos/" + id).asJson();
    }

    // Delete project to reset state of database (must delete both association and new object)
    public void deleteTodoTasksOfById(HttpResponse<JsonNode> response, int id)
    {
        int created_id = response.getBody().getObject().getInt("id");
        Unirest.delete("/todos/" + id + "/tasksof/" + created_id).asJson();
        Unirest.delete("/projects/" + created_id).asJson();
    }

    // Delete category to reset state of database (must delete both association and new object)
    public void deleteCategoryOfById(HttpResponse<JsonNode> response, int id)
    {
        int created_id = response.getBody().getObject().getInt("id");
        Unirest.delete("/todos/" + id + "/categories/" + created_id).asJson();
        Unirest.delete("/categories/" + created_id).asJson();
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
            .getJSONArray("tasksof").getJSONObject(0).getInt("id"), 1);
    }

    // HEAD /todos
    @Test
    public void testHeadTodos()
    {
        assertHeadStatusCode("/todos", STATUS_CODE_OK);
    }

    // POST /todos
    @Test
    public  void testPostTodosInvalidStatusCode() {
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
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Could not find field: inventedField");
    }

    @Test
    public void testPostTodosXMLStatusCodeInventedField()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos").header("Content-Type", "application/xml")
        .header("Content-Type", "application/xml")
        .body("<todo>\n    <title>Test429</title><description>false</description><inventedField>test</inventedField>\n</todo>\n").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_BAD_REQUEST);
    }

    // HEAD /todos:id
    @Test
    public void testHeadTodosWithId()
    {
        assertHeadStatusCode("/todos/1", STATUS_CODE_OK);
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
        .body("{\n\"doneStatus\": false,\n\"description\": \"\",\n\"title\": \"scan paperwork\",\n \"categories\": [\n{\n\"id\": \"1\"\n}\n],\n\"tasksof\": [\n{\n\"id\": \"1\"\n}\n]\n}")
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
        .body("{\n\"doneStatus\": false,\n\"description\": \"\",\n\"title\": \"scan paperwork\",\n \"categories\": [\n{\n\"id\": \"1\"\n}\n],\n\"tasksof\": [\n{\n\"id\": \"1\"\n}\n]\n}")
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
        .body("{\n\"doneStatus\": false,\n\"description\": \"\",\n\"title\": \"scan paperwork\",\n \"categories\": [\n{\n\"id\": \"1\"\n}\n],\n\"tasksof\": [\n{\n\"id\": \"1\"\n}\n]\n}")
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
        .body("{\n\"doneStatus\": false,\n\"description\": \"\",\n\"title\": \"scan paperwork\",\n \"categories\": [\n{\n\"id\": \"1\"\n}\n],\n\"tasksof\": [\n{\n\"id\": \"1\"\n}\n]\n}")
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

        response = Unirest.delete("/todos/" + id).header("Content-Type", "application/json")
        .asJson();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

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

        Unirest.delete("/todos/" + id).header("Content-Type", "application/json").asJson();

        response = Unirest.get("/todos").asJson();
        int new_size = response.getBody().getObject().getJSONArray("todos").length();


        assertEquals(original_size - new_size, 1);
    }

    // GET /todos/:id/tasksof
    @Test
    public void testGetTodosTaskOfStatusCode()
    {
        assertGetStatusCode("/todos/2/tasksof", STATUS_CODE_OK);
    }

    @Test
    public void testGetTodosTaskOfResponseSize()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos/1/tasksof").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("projects").length(), 1);
    }

    @Test
    public void testGetTodosTaskOfTitle()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos/1/tasksof").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("projects").getJSONObject(0).getString("title"), "Office Work");
    }

    @Test
    public void testGetTodosTaskOfCompleted()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos/1/tasksof").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("projects").getJSONObject(0).getString("completed"), "false");
    }

    @Test
    public void testGetTodosTaskOfTasksArray()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos/1/tasksof").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("projects").getJSONObject(0).getJSONArray("tasks").length(), 2);
    }

    // HEAD /todos/:id/tasksof
    @Test
    public void testHeadTodosTasksof()
    {
        assertHeadStatusCode("/todos/1/tasksof", STATUS_CODE_OK);
    }

    // POST /todos/:id/tasksof
    @Test
    public void testPostTodosTaskofInvalidErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/5/tasksof").asJson();
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Could not find parent thing for relationship todos/5/tasksof");
    }

    @Test
    public void testPostTodosTaskofInvalidStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/5/tasksof").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_NOT_FOUND);
    }

    @Test
    public void testPostTodosTaskofWithJSONIdStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/tasksof").body("{\n\"id\":1\n}\n").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_NOT_FOUND);
    }

    @Test
    public void testPostTodosTaskofWithJSONIdErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/tasksof").body("{\n\"id\":1\n}\n").asJson();
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Could not find thing matching value for id");
    }

    @Test
    public void testPostTodosTaskOfWithEmptyBodyStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/tasksof").asJson();
        
        // Delete to reset database to original state 
        deleteTodoTasksOfById(response, 1);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosTaskOfWithEmptyBodyTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/tasksof").asJson();
        
        // Delete to reset database to original state 
        deleteTodoTasksOfById(response, 1);

        assertEquals(response.getBody().getObject().getString("title"), "");
    }

    @Test
    public void testPostTodosTaskOfWithEmptyBodyCompletion()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/tasksof").asJson();
        
        // Delete to reset database to original state 
        deleteTodoTasksOfById(response, 1);

        assertEquals(response.getBody().getObject().getString("completed"), "false");
    }

    // DELETE /todos/:id/tasksof/:id
    @Test
    public void testDeleteTodosTaskofStatusCode()
    {
        // create todo taskof to delete
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/tasksof").asJson();
        
        int id = response.getBody().getObject().getInt("id");

        response = Unirest.delete("/todos/1/tasksof/" + id).header("Content-Type", "application/json")
        .asJson();

        // Must also delete project
        Unirest.delete("/projects/" + id).asJson();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }

    @Test
    public void testDeleteTodosTasksOfVerifyDeletion()
    {
        // create todo tasksof to delete
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/tasksof").asJson();
        
        int id = response.getBody().getObject().getInt("id");

        response = Unirest.get("/todos/1/tasksof").asJson();
        int original_size = response.getBody().getObject().getJSONArray("projects").length();

        Unirest.delete("/todos/1/tasksof/" + id).header("Content-Type", "application/json").asJson();

        // Must also delete project
        Unirest.delete("/projects/" + id).asJson();

        response = Unirest.get("/todos/1/tasksof").asJson();
        int new_size = response.getBody().getObject().getJSONArray("projects").length();

        assertEquals(original_size - new_size, 1);
    }

    // GET /todos/:id/categories
    @Test
    public void testGetTodosCategoriesStatusCode()
    {
        assertGetStatusCode("/todos/1/categories", STATUS_CODE_OK);
    }

    @Test
    public void testGetTodosCategoriesResponseSize()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos/1/categories").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("categories").length(), 1);
    }

    @Test
    public void testGetTodosCategoriesTitle()
    {
        HttpResponse<JsonNode> response = Unirest.get("/todos/1/categories").asJson();
        assertEquals(response.getBody().getObject().getJSONArray("categories").getJSONObject(0).getString("title"), "Office");
    }

    // HEAD /todos/:id/categories
    @Test
    public void testHeadTodosCategories()
    {
        assertHeadStatusCode("/todos/1/categories", STATUS_CODE_OK);
    }    

    // POST /todos/:id/categories
    @Test
    public void testPostTodosCategoriesWithJSONIdStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories").body("{\n\"id\":1\n}\n").asJson();
        
        assertEquals(response.getStatus(), STATUS_CODE_NOT_FOUND);
    }

    @Test
    public void testPostTodosCategoriesWithJSONIdErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories").body("{\n\"id\":1\n}\n").asJson();
        
        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Could not find thing matching value for id");
    }

    @Test
    public void testPostTodosTaskOfWithJSONBodyStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories").body("{\"title\":\"Test Category\"}").asJson();
        
        // Delete to reset database to original state 
        deleteCategoryOfById(response, 1);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosTaskOfWithJSONBodyTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories").body("{\"title\":\"Test Category\"}").asJson();
        
        // Delete to reset database to original state 
        deleteCategoryOfById(response, 1);

        assertEquals(response.getBody().getObject().getString("title"), "Test Category");
    }

    @Test
    public void testPostTodosTaskOfWithJSONBodyDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories").body("{\"title\":\"Test Category\"}").asJson();
        
        // Delete to reset database to original state 
        deleteCategoryOfById(response, 1);

        assertEquals(response.getBody().getObject().getString("description"), "");
    }

    @Test
    public void testPostTodosTaskOfWithXMLBodyStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories")
        .header("Content-Type", "application/xml").body("<category><title>Test Category</title></category>").asJson();
        
        // Delete to reset database to original state 
        deleteCategoryOfById(response, 1);

        assertEquals(response.getStatus(), STATUS_CODE_CREATED);
    }

    @Test
    public void testPostTodosTaskOfWithXMLBodyTitle()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories")
        .header("Content-Type", "application/xml").body("<category><title>Test Category</title></category>").asJson();
        
        // Delete to reset database to original state 
        deleteCategoryOfById(response, 1);

        assertEquals(response.getBody().getObject().getString("title"), "Test Category");
    }

    @Test
    public void testPostTodosTaskOfWithXMLBodyDescription()
    {
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories")
        .header("Content-Type", "application/xml").body("<category><title>Test Category</title></category>").asJson();
        
        // Delete to reset database to original state 
        deleteCategoryOfById(response, 1);

        assertEquals(response.getBody().getObject().getString("description"), "");
    }

    // DELETE /todos/:id/categories/:id
    @Test
    public void testDeleteTodosCategoriesStatusCode()
    {
        // create todo category to delete
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories")
        .header("Content-Type", "application/xml").body("<category><title>Test Category</title></category>").asJson();
                
        int id = response.getBody().getObject().getInt("id");

        response = Unirest.delete("/todos/1/categories/" + id).header("Content-Type", "application/json")
        .asJson();

        // Must also delete category
        Unirest.delete("/categories/" + id).asJson();

        assertEquals(response.getStatus(), STATUS_CODE_OK);
    }
  
    @Test
    public void testDeleteTodosCategoriesVerifyDeletion()
    {
        // create todo tasksof to delete
        HttpResponse<JsonNode> response = Unirest.post("/todos/1/categories")
        .header("Content-Type", "application/xml").body("<category><title>Test Category</title></category>").asJson();
                
        int id = response.getBody().getObject().getInt("id");

        response = Unirest.get("/todos/1/categories").asJson();
        int original_size = response.getBody().getObject().getJSONArray("categories").length();

        Unirest.delete("/todos/1/categories/" + id).header("Content-Type", "application/json").asJson();

        // Must also delete category
        Unirest.delete("/categories/" + id).asJson();

        response = Unirest.get("/todos/1/categories").asJson();
        int new_size = response.getBody().getObject().getJSONArray("categories").length();

        assertEquals(original_size - new_size, 1);
    }

    @Test
    public void testDeleteInvalidTodosCategoriesStatusCode()
    {
        HttpResponse<JsonNode> response = Unirest.delete("/todos/1/categories/8").header("Content-Type", "application/json")
        .asJson();

        assertEquals(response.getStatus(), STATUS_CODE_NOT_FOUND);
    }

    @Test
    public void testDeleteInvalidTodosCategoriesErrorMessage()
    {
        HttpResponse<JsonNode> response = Unirest.delete("/todos/1/categories/8").header("Content-Type", "application/json")
        .asJson();

        assertEquals(response.getBody().getObject().getJSONArray("errorMessages").getString(0), "Could not find any instances with todos/1/categories/8");
    }
}